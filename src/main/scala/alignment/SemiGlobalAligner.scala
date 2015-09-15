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

    // First row
    for (j <- 0 to this.n1)
      scores(0)(j) = 0

    // First column
    for (i <- 1 to this.n2)
      scores(i)(0) = i * indelScore

    // Rest of the matrix
    for (i <- 1 to this.n2)
      for (j <- 1 to this.n1)
        scores(i)(j) = this.cost(scores, i, j, this.sequence1(j - 1), this.sequence2(i - 1))

    scores
  }

  /** @see alignment.Aligner.showAlignment() */
  def showAlignment: String = {
    def backtrace(i: Int, j: Int, s1: String, s2: String, s3: String): (String, String, String) = {
      if (s2.length >= this.sequence2.length && s2(0) == this.sequence2(0))
        (s1, s2, s3)
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
    val (s1, s2, middle) = backtrace(this.n2, minScoreIndex, "", "", "")

    s1 + "\n" + middle + "\n" + s2
  }

}
