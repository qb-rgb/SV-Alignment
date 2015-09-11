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

}
