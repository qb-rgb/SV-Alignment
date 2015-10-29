package object indexation {

  type SuffixTable = List[Int]

  // Compare two section of the genome without using the substring method
  private def indexCompare(
    genome: String,
    iinit: Int,
    jinit: Int,
    i: Int,
    j: Int
  ): Boolean =
    if (j >= genome.length || i >= genome.length)
      i > j
    else {
      val gi = genome(i)
      val gj = genome(j)

      if (gi == gj)
        indexCompare(genome, iinit, jinit, i + 1, j + 1)
      else
        gi < gj
    }

    // startsWith method without copying the strings
  private def indexStartsWith(
    genome: String,
    read: String,
    gi: Int,
    ri: Int
  ): Boolean =
    if (ri >= read.length)
      true
    else if (gi >= genome.length || genome(gi) != read(ri))
      false
    else
      indexStartsWith(genome, read, gi + 1, ri + 1)

  /** Build the suffix table from a genome.
    *
    * @param genome genome from which build the suffix table
    * @return suffix table of the genome
    */
  def buildSuffixTableFrom(genome: String): SuffixTable =
    (0 until genome.length).toList sortWith {
      (i, j) => indexCompare(genome, i, j, i, j)
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

        if (indexStartsWith(genome, read, st(next), 0))
          getBorder(next, cond, f, st)
        else
          i
      }

    // Make a binary search to find the interval of indexes
    def binarySearch(suffixTable: SuffixTable, i: Int): Option[(Int, Int)] = {
      val n = suffixTable.length
      val m = n / 2
      // Remplacer le substring par un drop
      val w = genome.substring(suffixTable(m))

      if (suffixTable.isEmpty)
        None
      // else if (w startsWith read)
      else if (indexStartsWith(genome, read, suffixTable(m), 0))
        Some((
          i + getBorder(m, { _ == 0 }, { _ - 1 }, suffixTable),
          i + getBorder(m, { _ == n - 1 }, { _ + 1 }, suffixTable)
        ))
      else if (w < read)
        binarySearch(suffixTable drop m, i + m)
      else
        binarySearch(suffixTable take m, i)
    }

    binarySearch(suffixTable, 0)
  }

}
