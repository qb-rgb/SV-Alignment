package search

import indexation.Indexer

/** Seek exactly a sequence in an indexed genome. */
class ExactSeeker(val indexer: Indexer) extends Seeker {

  /** @see search.Seeker.seekSequence() */
  def seekSequence(sequence: String): Iterator[Int] =
    this.indexer getSequenceLocation sequence

}
