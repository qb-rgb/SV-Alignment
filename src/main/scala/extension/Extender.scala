package extension

import align.{SemiGlobalAligner, Alignment, countNucs}
import search.Seeker

/** Make an extension on a read. */
class Extender(
  genome: String,
  seeker: Seeker,
  matchScore: Int,
  mismatchScore: Int,
  indelScore: Int,
  error: Int,
  errorPercentage: Int
) {

  // Check if an alignment is valid or not
  private def checkAlignment(alignment: Alignment): Boolean =
    (alignment.countMatches * 100) / alignment.sequence1.length >= (100 - this.errorPercentage)

  // Correct an alignment
  private def correctAlignment(alignment: Alignment): Alignment = {
    val zipSeqs = alignment.sequence1 zip alignment.sequence2
    val corrSeqs = (zipSeqs.reverse dropWhile {
      case (_, c2) => c2 == '-'
    }).reverse.unzip
    val (newSeq1, newSeq2) = (corrSeqs._1.mkString, corrSeqs._2.mkString)

    new Alignment(
      alignment.beginIndex1,
      newSeq1,
      alignment.beginIndex2,
      newSeq2
    )
  }

  // Link two alignments
  private def linkAlignments(left: Alignment, right: Alignment, seed: String): Alignment = {
    val a = new Alignment(
      left.beginIndex1,
      left.sequence1 + seed + right.sequence1,
      left.beginIndex2,
      left.sequence2 + seed + right.sequence2
    )
    println("final alignment: \n" + a)
    a
  }

  // Create an alignment between a read and the genome using a seed
  private def extendForPosition(
    read: String,
    seed: String,
    seedPos: Int,
    posInGen: Int
  ): Option[Alignment] = {
    // Read parts
    val rightRead = read.substring(seedPos + seed.length)
    val leftRead = read.substring(0, seedPos)

    println("------" + posInGen + "------")
    println("left read: " + leftRead)
    println("right read: " + rightRead)

    // Genome parts
    val leftGenIndex = math.max(posInGen - 2 * leftRead.length, 0)
    val leftGen = genome.substring(leftGenIndex, posInGen)
    val rightGenBeginIndex = posInGen + seed.length
    val rightGenIndex = math.min(rightGenBeginIndex + 2 * rightRead.length, genome.length)
    val rightGen = genome.substring(rightGenBeginIndex, rightGenIndex)

    println("left genome: " + leftGen)
    println("right genome: " + rightGen)

    // If the alignment is not doable for one or other parts
    if (leftRead.length > leftGen.length || rightRead.length > rightGen.length)
      None
    else {
      val leftAlignment = new SemiGlobalAligner(
        posInGen - 1,
        leftGen.reverse, leftRead.reverse,
        this.matchScore, this.mismatchScore, this.indelScore,
        this.error
      ).alignment
      val rightAlignment = new SemiGlobalAligner(
        rightGenIndex,
        rightGen, rightRead,
        this.matchScore, this.mismatchScore, this.indelScore,
        this.error
      ).alignment

      val corrLeftAlignment = {
        val inverse = this.correctAlignment(leftAlignment)
        new Alignment(
          posInGen - 1 - countNucs(inverse.sequence1),
          inverse.sequence1.reverse,
          0,
          inverse.sequence2.reverse
        )
      }
      val corrRightAlignment = this.correctAlignment(rightAlignment)

      println("leftAlignment: \n" + leftAlignment)
      println("rightAlignment: \n" + rightAlignment)
      println("corrLeftAlignment: \n" + corrLeftAlignment)
      println("corrRightAlignment: \n" + corrRightAlignment)

      val finalAlignment = this.linkAlignments(corrLeftAlignment, corrRightAlignment, seed)
      if (this checkAlignment finalAlignment)
        Some(finalAlignment)
      else
        None
    }
  }

  /** Generate all the acceptable alignments for a read.
    *
    * @param read read to align
    * @param seed seed extract from the read
    * @param seedPos positions of the seed in the read
    */
  def extend(read: String, seed: String, seedPos: List[Int]): List[Alignment] = {
    val seedPositionsInGenome = this.seeker seekSequence seed
    println("seed positions: " + seedPositionsInGenome)
    val alignments = for {
      pos <- seedPos
      inGen <- seedPositionsInGenome
    } yield this.extendForPosition(read, seed, pos, inGen)

    println("Final alignments: " + alignments)

    (alignments filter { _.isDefined }) map { _.get }
  }

}
