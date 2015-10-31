package alignment

/** Class to represent semi global aligners.
  *
  * @constructor create a new aligner.
  * @param sequence1 first sequence to align
  * @param sequence2 second sequence to align
  * @param matchScore score of a match
  * @param mismatchScore score of a mismatch
  * @param indelScore score of an insertion/deletion
  *
  * @author Quentin Baert
  */
class SemiGlobalAligner(
  override val sequence1: String,
  override val sequence2: String,
  override val matchScore: Int,
  override val mismatchScore: Int,
  override val indelScore: Int,
  override val error: Int
) extends Aligner(sequence1, sequence2, matchScore, mismatchScore, indelScore, error) {

  /** @see alignment.Aligner.align() */
  override protected def align: Array[Array[Int]] = {
    val scores = Array.ofDim[Int](this.n2 + 1, this.n1 + 1)
    val minusInfinity = -100000

    // First row
    for (j <- 0 to this.error)
      scores(0)(j) = 0

    // First column
    for (i <- 1 to this.error)
      scores(i)(0) = i * indelScore

    // Diagonals
    val diag1Couples = (this.error + 1 to this.n2) zip (0 to this.error)
    val diag2Couples = (0 to this.n2) zip (this.error + 1 to this.n1)

    for ((i, j) <- diag1Couples) scores(i)(j) = minusInfinity
    for ((i, j) <- diag2Couples) scores(i)(j) = minusInfinity

    // Rest of the matrix in the band of width k where k is the error of the aligner
    for (i <- 1 to this.n2)
      for (j <- math.max(1, i - this.error) to math.min(i + this.error, this.n1))
        scores(i)(j) = this.cost(scores, i, j, this.sequence1(j - 1), this.sequence2(i - 1))

    scores
  }

  /** @see alignment.Aligner.showAlignment() */
  def showAlignment: String = {
    def backtrace(i: Int, j: Int, s1: String, s2: String, s3: String): (String, String, String, Int) = {
      if (i == 0)
        (s1, s2, s3, j)
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

        if (thisIsMatch || actual == upLeft + this.mismatchScore) {
          val middleChar = if (thisIsMatch) "|" else " "

          backtrace(upLeftI, upLeftJ,
                    this.sequence1(j - 1) + s1,
                    this.sequence2(i - 1) + s2,
                    middleChar + s3)
        } else if (actual == left + this.indelScore)
          backtrace(leftI, leftJ,
                    this.sequence1(j - 1) + s1,
                    "-" + s2,
                    " " + s3)
        else
          backtrace(upI, upJ,
                    "-" + s1,
                    this.sequence2(i - 1) + s2,
                    " " + s3)
      }
    }

    val minScoreIndex = this.alignmentMatrix(this.n2).zipWithIndex.max._2
    val (s1, s2, middle, beginIndex) = backtrace(this.n2, minScoreIndex, "", "", "")
    val one = 1
    val elevenSpaces = " " * 11

    f"${beginIndex + 1}%10d " + s1 + "\n" + elevenSpaces + middle + "\n" + f"$one%10d " + s2
  }

}
