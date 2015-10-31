package extension

/** Extract the minimal amount of seeds from a read. */
object MinimalSeedExtracter extends SeedExtracter {

  /** @see extension.SeedExtracter.extractSeedsFrom() */
  def extractSeedsFrom(read: String, length: Int): Map[String, List[Int]] = {
    val seeds = this.getSeedsByStep(read, length, length)
    val lastIndex = read.length - length

    if (seeds.values exists { _ contains lastIndex })
      seeds
    else {
      val lastSeed = read drop lastIndex

      if (seeds contains lastSeed)
        seeds.updated(lastSeed, lastIndex :: seeds(lastSeed))
      else
        seeds + (lastSeed -> List(lastIndex))
    }
  }

}
