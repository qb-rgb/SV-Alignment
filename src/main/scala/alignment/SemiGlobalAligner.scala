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
  override val brink: Int
) extends Aligner(sequence1, sequence2, matchScore, mismatchScore, indelScore, brink) {

  /** @see alignment.Aligner.align() */
  override protected def align: Array[Array[Int]] = {
    val scores = Array.ofDim[Int](this.n2 + 1, this.n1 + 1)

    // First row
    for (j <- 0 to this.n1)
      scores(0)(j) = -j

    // First column
    for (i <- 0 to this.n2)
      scores(i)(0) = -i

    // Rest of the matrix
    for (i <- 1 to this.n2)
      for (j <- 1 to this.n1)
        scores(i)(j) = this.cost(scores, i, j, this.sequence1(j - 1), this.sequence2(i - 1))

    scores
  }

  /** @see alignment.Aligner.showAlignment() */
  def showAlignment: String = {
    def backtrace(
      actualI: Int,
      actualJ: Int,
      upI: Int,
      upJ: Int,
      leftI: Int,
      leftJ: Int,
      upLeftI: Int,
      upLeftJ: Int,
      alignedSequence1: String,
      alignedSequence2: String): String = {

      if (alignedSequence2.length >= this.sequence2.length &&
          alignedSequence2(0) == this.sequence2(0))
        alignedSequence1 + "\n" + alignedSequence2
      else {
        val actual = this.alignmentMatrix(actualI)(actualJ)
        val left = this.alignmentMatrix(leftI)(leftJ)
        val upLeft = this.alignmentMatrix(upLeftI)(upLeftJ)

        val thisIsMatch =
          (actual == upLeft + this.matchScore) &&
          (this.sequence1(actualJ - 1) == this.sequence2(actualI - 1))

        if (thisIsMatch || actual == upLeft + this.mismatchScore)
          backtrace(
            upLeftI,
            upLeftJ,
            upLeftI - 1,
            upLeftJ,
            upLeftI,
            upLeftJ - 1,
            upLeftI - 1,
            upLeftJ - 1,
            this.sequence1(actualJ - 1) + alignedSequence1,
            this.sequence2(actualI - 1) + alignedSequence2
          )
        else if (actual == left + this.indelScore)
          backtrace(
            leftI,
            leftJ,
            upLeftI,
            upLeftJ,
            leftI,
            leftJ - 1,
            upLeftI,
            upLeftJ - 1,
            this.sequence1(actualJ - 1) + alignedSequence1,
            "-" + alignedSequence2
          )
        else
          backtrace(
            upI,
            upJ,
            upI - 1,
            upJ,
            upLeftI,
            upLeftJ,
            upLeftI - 1,
            upLeftJ,
            "-" + alignedSequence1,
            this.sequence2(actualI - 1) + alignedSequence2
          )
      }

    }

    val minScoreIndex = this.alignmentMatrix(this.n2).zipWithIndex.max._2

    backtrace(
      this.n2,
      minScoreIndex,
      this.n2 - 1,
      minScoreIndex,
      this.n2,
      minScoreIndex - 1,
      this.n2 - 1,
      minScoreIndex - 1,
      "",
      ""
    )
  }

}
