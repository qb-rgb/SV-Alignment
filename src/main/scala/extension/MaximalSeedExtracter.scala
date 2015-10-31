package extension

/** Extract the maximal amount of seeds from a read. */
object MaximalSeedExtracter extends SeedExtracter {

  /** @see extension.SeedExtracter.extractSeedsFrom() */
  def extractSeedsFrom(read: String, length: Int): Map[String, List[Int]] =
    this.getSeedsByStep(read, length, 1)

}
