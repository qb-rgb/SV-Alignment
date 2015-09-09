/**
  * Class that represents objects which aligns two sequences
  *
  * @author Quentin Baert
  */
abstract class Aligner(
  val sequence1: String,
  val sequence2: String,
  val matchScore: Int,
  val mismatchScore: Int,
  val indelScore: Int,
  val brink: Int
) {

  protected val n1: Int = sequence1.length
  protected val n2: Int = sequence2.length

  assert(n1 >= n2)

  protected def cost(array: Array[Array[Int]], i: Int, j: Int, char1: Char, char2: Char): Int = {
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

  def align: Array[Array[Int]]

}
