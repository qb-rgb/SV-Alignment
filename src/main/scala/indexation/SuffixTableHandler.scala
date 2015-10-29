package indexation

class SuffixTableHandler(val genome: String) {

  // Compare two section of the genome without using the substring method
  def indexCompare(s1: String, s2: String, i: Int, j: Int): Boolean = {
    def innerIndexCompare(
      s1: String,
      s2: String,
      iinit: Int,
      jinit: Int,
      i: Int,
      j: Int
    ): Boolean = {
      val ends1 = i >= s1.length

      if (j >= s2.length || ends1)
        ends1
      else {
        val s1i = s1(i)
        val s2j = s2(j)

        if (s1i == s2j)
          innerIndexCompare(s1, s2, iinit, jinit, i + 1, j + 1)
        else
          s1i < s2j
      }
    }

    innerIndexCompare(s1, s2, i, j, i, j)
  }

  // Check if a string starts with another
  private def indexStartsWith(s1: String, s2: String, i: Int, j: Int): Boolean =
    if (j >= s2.length)
      true
    else if (i >= s1.length || s1(i) != s2(j))
      false
    else
      indexStartsWith(s1, s2, i + 1, j + 1)

  /** Suffix table build from the SuffixTableHandler genome. */
  val suffixTable: SuffixTable =
    (0 until this.genome.length).toList sortWith {
      (i, j) => indexCompare(this.genome, this.genome, i, j)
    }

  /** Make a binary search to get the indexes of a sequence in the suffix table.
    *
    * @param sequence sequence to find
    * @return indexes showing the interval of the suffix table in which the
    *         sequence is.
    */
  def getSequenceLocation(sequence: String): Option[(Int, Int)] = {
    // Search read position around a value
    def getBorder(i: Int, cond: Int => Boolean, f: Int => Int, st: SuffixTable): Int =
      if (cond(i))
        i
      else {
        val next = f(i)

        if (this.indexStartsWith(this.genome, sequence, st(next), 0))
          getBorder(next, cond, f, st)
        else
          i
      }

    // Make a binary search to find the interval of indexes
    def binarySearch(st: SuffixTable, i: Int): Option[(Int, Int)] = {
      val n = st.length
      val m = n / 2

      if (this.indexStartsWith(this.genome, sequence, st(m), 0))
        Some((
          i + getBorder(m, { _ == 0 }, { _ - 1 }, st),
          i + getBorder(m, { _ == n - 1 }, { _ + 1 }, st)
        ))
      else if (n <= 1)
        None
      else if (this.indexCompare(this.genome, sequence, st(m), 0))
        binarySearch(st drop m, i + m)
      else
        binarySearch(st take m, i)
    }

    binarySearch(this.suffixTable, 0)
  }

}
