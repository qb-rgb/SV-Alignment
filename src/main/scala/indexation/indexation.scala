package object indexation {

  type SuffixTable = List[Int]

  /**
    * Build the suffix table from a genome.
    *
    * @param genome genome from which build the suffix table
    * @return suffix table of the genome
    */
  /*
  def buildSuffixTableFrom(genome: String): SuffixTable = {
    val n = genome.length
    (0 until genome.length).toList sortWith {
      (i, j) => genome.substring(i) < genome.substring(j)
    }
  }
  */

  def f(genome: String, iinit: Int, jinit: Int, i: Int, j: Int): Boolean = {
    if (i >= genome.length || j >= genome.length)
      iinit + i < jinit + j
    else {
      val gi = genome(i)
      val gj = genome(j)

      if (gi == gj)
        f(genome, iinit, jinit, i + 1, j + 1)
      else
        gi < gj
    }
  }

  def buildSuffixTableFrom(genome: String): SuffixTable = {
    (0 until genome.length).toList sortWith {
      (i, j) => f(genome, i, j, i, j)
    }
  }

  /** Make a binary search to get the indexes of a read in the suffix table.
    *
    * @param read read to find
    * @param genome genome from which the suffix table was built
    * @param suffixTable suffix table of the previous genome
    * @return indexes showing the interval of the suffix table in which the read
    *         is.
    */
  def getIndexOfReadInSuffixTable(
    read: String,
    genome: String,
    suffixTable: SuffixTable
  ): Option[(Int, Int)] = {
    // Search read position around a value
    def getBorder(i: Int, cond: Int => Boolean, f: Int => Int, st: SuffixTable): Int =
      if (cond(i))
        i
      else {
        val next = f(i)

        if (genome.substring(st(next)) startsWith read)
          getBorder(next, cond, f, st)
        else
          i
      }

    // Make a binary search to find the interval of indexes
    def binarySearch(suffixTable: SuffixTable): Option[(Int, Int)] = {
      val n = suffixTable.length
      val m = n / 2
      val w = genome.substring(suffixTable(m))

      if (suffixTable.isEmpty)
        None
      else if (w startsWith read)
        Some((
          getBorder(m, { _ == 0 }, { _ - 1 }, suffixTable),
          getBorder(m, { _ == n - 1 }, { _ + 1 }, suffixTable)
        ))
      else if (w < read)
        binarySearch(suffixTable drop m)
      else
        binarySearch(suffixTable take m)
    }

    binarySearch(suffixTable)
  }

}
