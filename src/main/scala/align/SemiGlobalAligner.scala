package align

/** Class to represent semi global aligners.
  *
  * @constructor create a new aligner.
  * @param sequence1BeginIndex begining index of the sequence1
  * @param sequence1 first sequence to align
  * @param sequence2 second sequence to align
  * @param matchScore score of a match
  * @param mismatchScore score of a mismatch
  * @param indelScore score of an insertion/deletion
  *
  * @author Quentin Baert
  */
class SemiGlobalAligner(
  val sequence1BeginIndex: Int,
  override val sequence1: String,
  override val sequence2: String,
  override val matchScore: Int,
  override val mismatchScore: Int,
  override val indelScore: Int,
  override val error: Int
) extends Aligner(sequence1, sequence2, matchScore, mismatchScore, indelScore, error) {

  /** @see align.Aligner.alignmentMatrix() */
  protected val alignmentMatrix: Array[Array[Int]] = {
    val scores = Array.ofDim[Int](this.n2 + 1, this.n1 + 1)
    val minusInfinity = -100000

    // First row
    for (j <- 0 to math.min(this.error, this.n1))
      scores(0)(j) = 0

    // First column
    for (i <- 1 to math.min(this.error, this.n2))
      scores(i)(0) = i * indelScore

    // Diagonals
    val diag1Couples = (this.error + 1 to this.n2) zip (0 to this.n1)
    val diag2Couples = (0 to this.n2) zip (this.error + 1 to this.n1)

    for ((i, j) <- diag1Couples) scores(i)(j) = minusInfinity
    for ((i, j) <- diag2Couples) scores(i)(j) = minusInfinity

    // Rest of the matrix in the band of width k where k is the error of the aligner
    for (i <- 1 to this.n2)
      for (j <- math.max(1, i - this.error) to math.min(i + this.error, this.n1))
        scores(i)(j) = this.cost(scores, i, j, this.sequence1(j - 1), this.sequence2(i - 1))

    scores
  }

  /** @see align.Aligner.align() */
  val alignment: Alignment = {
    def backtrace(i: Int, j: Int, s1: String, s2: String): (String, String, Int) = {
      if (i == 0 && j == 0)
        (s1, s2, this.sequence1BeginIndex + j)
      else if (i == 0)
        backtrace(i, j - 1, this.sequence1(j - 1) + s1, "-" + s2)
      else if (j == 0)
        backtrace(i - 1, j, "-" + s1, this.sequence2(i - 1) + s2)
      else {
        val (upI, upJ) = (i - 1, j)
        val (leftI, leftJ) = (i, j - 1)
        val (upLeftI, upLeftJ) = (upI, leftJ)
        val actual = this.alignmentMatrix(i)(j)
        val left = this.alignmentMatrix(leftI)(leftJ)
        val upLeft = this.alignmentMatrix(upLeftI)(upLeftJ)

        val thisIsMatch =
          (actual == upLeft + this.matchScore) &&
          (this.sequence1(j - 1) == this.sequence2(i - 1))

        if (thisIsMatch || actual == upLeft + this.mismatchScore)
          backtrace(upLeftI, upLeftJ,
                    this.sequence1(j - 1) + s1,
                    this.sequence2(i - 1) + s2)
        else if (actual == left + this.indelScore)
          backtrace(leftI, leftJ,
                    this.sequence1(j - 1) + s1,
                    "-" + s2)
        else
          backtrace(upI, upJ,
                    "-" + s1,
                    this.sequence2(i - 1) + s2)
      }
    }

    def findMax(line: List[(Int, Int)], max: Int, res: Int): Int =
      if (line.isEmpty)
        res
      else {
        val (value, index) = line.head

        if (value > max)
          findMax(line.tail, value, index)
        else
          findMax(line.tail, max, res)
      }

    val minInter = math.max(0, this.n2 - this.error)
    val valueAndIndex = this.alignmentMatrix(this.n2).
                        zipWithIndex.
                        drop(minInter).toList
    val maxScoreIndex = findMax(
      valueAndIndex.tail, valueAndIndex.head._1, valueAndIndex.head._2
    )
    val initS1 = (
      for (i <- this.n1 - 1 to maxScoreIndex by -1) yield this.sequence1(i)
    ).reverse
    val initS2 = "-" * (this.n1 - maxScoreIndex)
    val (s1, s2, beginIndex) = backtrace(this.n2, maxScoreIndex, initS1.mkString ,initS2)

    new Alignment(beginIndex + 1, s1, 0, s2)
  }

}
