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
    val dim1 = this.n1 + 1
    val dim2 = this.n2 + 1
    val scores = Array.ofDim[Int](dim2, dim1)

    for (j <- 0 until dim1)
      scores(0)(j) = -j

    for (i <- 0 until dim2)
      scores(i)(0) = -i

    for (i <- 1 until dim2)
      for (j <- 1 until dim1)
        scores(i)(j) = this.cost(scores, i, j, this.sequence1(j - 1), this.sequence2(i - 1))

    scores
  }

}
