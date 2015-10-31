package indexation

/** Represent a indexer. An indexer index a genome. */
trait Indexer {

  /** Find the locations of a sequence in the index genome.
    *
    * @param sequence sequence to find
    * @return iterator of indexes of the sequence in the genome
    */
  def getSequenceLocation(sequence: String): List[Int]

}
