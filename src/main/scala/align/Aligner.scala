package align

/** Abstract class to represent aligners.
  * An aligner aligns two sequences with match, mismatch and insertion/deletion.
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
abstract class Aligner(
  val sequence1: String,
  val sequence2: String,
  val matchScore: Int,
  val mismatchScore: Int,
  val indelScore: Int,
  val error: Int
) {

  // Length of the first sequence.
  protected val n1: Int = sequence1.length

  // Length of the second sequence.
  protected val n2: Int = sequence2.length

  // It is assumed that the first sequence is longer than the second
  assert(this.n1 >= this.n2)

  // It is useless to align a empty sequence
  // assert(this.n1 != 0 && this.n2 != 0)

  // Not more error than the length of the smallest sequence
  // assert(this.error <= this.n2)

  /** Cost function of the aligner.
    *
    * @param array matrix for the dynamique programming
    * @param i row of the matrix in the recursion
    * @param j column of the matrix in the recursion
    * @param char1 char of the first sequence actually aligned
    * @param char2 char of the second sequence actually aligned
    * @return best score to put in the dynamique programming matrix
    */
  protected def cost(array: Array[Array[Int]], i: Int, j: Int, char1: Char, char2: Char): Int = {
    def max3(x: Int, y: Int, z: Int): Int = math.max(x, math.max(y, z))

    val matchOrMisMatch =
      if (char1 == char2)
        array(i - 1)(j - 1) + this.matchScore
      else
        array(i - 1)(j - 1) + this.mismatchScore
    val indel1 = array(i)(j - 1) + indelScore
    val indel2 = array(i - 1)(j) + indelScore

    max3(matchOrMisMatch, indel1, indel2)
  }

  /** Create the alignment matrix for the two sequences of the aligner.
    *
    * @return alignment for the two sequences of the aligner
    */
  def alignment: Alignment

  /** Give the alignement matrix for the two sequences of the aligner.
    *
    * @return alignment matrix for the two sequences of the aligner
    */
  protected def alignmentMatrix: Array[Array[Int]]

  /** Show the alignment matrix of the alignment.
    *
    * @return string which represents the alignment matrix
    */
  def showAlignmentMatrix: String = {
    def putSpacesOnLine(line: Array[Int]): String =
      (line map (x => f"$x%8d")).mkString

    (this.alignmentMatrix map (x => putSpacesOnLine(x))) mkString "\n"
  }

}
