package align

/** Represent an alignment between two sequences.
  *
  * @constructor create a new alignment
  * @param beginIndex1 index of the begining for the first sequence
  * @param sequence1 first sequence of the alignment
  * @param beginIndex2 index of the begining for the second sequence
  * @param sequence2 second sequence of the alignment
  */
class Alignment(
  val beginIndex1: Int,
  val sequence1: String,
  val beginIndex2: Int,
  val sequence2: String
) {

  import scala.collection.immutable.Queue

  // For an alignment, the two sequence must have the same length
  require(this.sequence1.length == this.sequence2.length)

  // Pairs of nucleotide of the alignment
  private val nucleotidePairs = this.sequence1 zip this.sequence2

  /** Count of matches in the alignment. */
  lazy val countMatches: Int = this.nucleotidePairs.foldLeft(0){
    case (acc, (nuc1, nuc2)) => if (nuc1 == nuc2) acc + 1 else acc
  }

  /** Count of gaps in the alignment. */
  lazy val countGaps: Int = this.nucleotidePairs.foldLeft(0){
    case (acc, ('-', _)) => acc + 1
    case (acc, (_, '-')) => acc + 1
    case (acc, _)        => acc
  }

  override def toString: String = {
    val links = this.nucleotidePairs map {
      case (nuc1, nuc2) =>
        if (nuc1 == nuc2)
          "|"
        else
          " "
    }

    def processSeq(seq: String, start: Int): List[String] = {
      def count(s: String, total: Int): (String, Int) = {
        val nucs = countNucs(s)
        val oldTotal =
          if (nucs != 0)
            total
          else
            total - 1
        val newTotal =
          if (nucs != 0)
            total + nucs - 1
          else
            total
        (f"${oldTotal}%7d $s", newTotal)
      }

      def innerProcess(
        l: List[String],
        res: Queue[String],
        total: Int
      ): List[String] =
        if (l.isEmpty)
          res.toList
        else {
          val (line, newTotal) = count(l.head, total + 1)
          innerProcess(l.tail, res :+ line, newTotal)
        }

      innerProcess((seq grouped 50).toList, Queue[String](), 0)
    }

    val splitSeq1 = processSeq(this.sequence1, this.beginIndex1)
    val splitSeq2 = processSeq(this.sequence2, this.beginIndex2)
    val splitLinks = (links.mkString grouped 50).toList map { "        " + _ }

    val lines = (splitSeq1 zip splitLinks zip splitSeq2) map {
      case ((lines1, lines2), lines3) => (lines1, lines2, lines3)
    }

    lines.foldLeft(""){
      case (acc, (seq1, l, seq2)) => acc + seq1 + "\n" + l + "\n" + seq2 + "\n"
    }
  }

}
