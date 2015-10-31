package search

/** Seek sequence in an indexed genome. */
trait Seeker {

  import indexation.Indexer

  /** Reference indexer of the seeker. */
  def indexer: Indexer

  /** Seek a sequence in a indexed genome.
    *
    * @param sequence sequence to seek
    * @return list of indexes of the sequence in the genome
    */
  def seekSequence(sequence: String): List[Int]

}
