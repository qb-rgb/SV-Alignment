class SemiGlobalAligner(
  override val sequence1: String,
  override val sequence2: String,
  override val matchScore: Int,
  override val mismatchScore: Int,
  override val indelScore: Int,
  override val brink: Int
) extends Aligner(sequence1, sequence2, matchScore, mismatchScore, indelScore, brink) {

  override def align: Array[Array[Int]] = {
    val dim1 = this.n1 + 1
    val dim2 = this.n2 + 1
    val scores = Array.ofDim[Int](dim1, dim2)

    for (i <- 0 until dim1)
      scores(i)(0) = -i

    for (j <- 0 until dim2)
      scores(0)(j) = -j

    for (i <- 1 until dim1) {
      for (j <- 1 until dim2) {
        scores(i)(j) = this.cost(scores, i, j, this.sequence1(i - 1), this.sequence2(j - 1))
      }
    }

    scores
  }

}
