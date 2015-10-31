package extension

/** Make seeds from a read. */
abstract class SeedExtracter {

  /** Extract seeds by step from a read.
    *
    * @param read read from which extract the seeds
    * @param length length of the seeds to extract
    * @param step step used to extract the seeds
    * @return list of seeds extracted from the read with their positions in the
    *         read
    */
  protected def getSeedsByStep(read: String, length: Int, step: Int): Map[String, List[Int]] = {
    val seeds = for {
      i <- 0 to read.length - length by step
    } yield (read.substring(i, i + length), List(i))

    seeds.foldLeft(Map[String, List[Int]]()) {
      case (acc, (seed, list@List(index))) =>
        if (acc contains seed)
          acc.updated(seed, index :: acc(seed))
        else
          acc + (seed -> list)
    }
  }

  /** Extract seeds from a read.
    *
    * @param read read from which extract the seeds
    * @param length length of the seeds to extract
    * @return list of seeds extracted from the read with their positions in the
    *         read
    */
  def extractSeedsFrom(read: String, length: Int): Map[String, List[Int]]

}
