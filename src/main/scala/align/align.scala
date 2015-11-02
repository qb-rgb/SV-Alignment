package object align {

  /** Count the number of nucleotides in a sequence.
    *
    * @param sequence sequence in which count the nucleotides
    * @return number of nucleotides in the sequence
    */
  def countNucs(sequence: String): Int =
    (sequence filterNot { _ == '-' }).length

}
