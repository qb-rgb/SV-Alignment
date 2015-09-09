/**
  * Class that represents objects which aligns two sequences
  *
  * @author Quentin Baert
  */
class Aligner(
  val sequence1: String,
  val sequence2: String,
  val matchScore: Int,
  val mismatchScore: Int,
  val indelScore: Int,
  val brink: Int
) {

  private val n1: Int = sequence1.length
  private val n2: Int = sequence2.length

  assert(n1 >= n2)

  private def cost(array: Array[Array[Int]], i: Int, j: Int, char1: Char, char2: Char): Int = {
    def max3(x: Int, y: Int, z: Int): Int =
      math.max(x, math.max(y, z))

    val matchOrMisMatch =
      if (char1 == char2)
        array(i - 1)(j - 1) + this.matchScore
      else
        array(i - 1)(j - 1) + this.mismatchScore
    val indel1 = array(i)(j - 1) + indelScore
    val indel2 = array(i - 1)(j) + indelScore

    max3(matchOrMisMatch, indel1, indel2)
  }

  def align: Array[Array[Int]] = {
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
