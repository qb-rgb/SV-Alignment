package fasta

/** Object that extract a reads from a FASTQ file.
  *
  * @author Quentin Baert
  */
object FASTQReader {

  import scala.io.Source

  /** Extract reads from a FASTQ file.
    *
    * @param fileName file name of the FASTQ file
    * @return reads extracted from the FASTQ file
    */
  def readFrom(fileName: String): List[String] = {
    val source = Source fromFile fileName
    val lines = try source.getLines.toList finally source.close

    ((lines grouped 4) map { case List(l1, l2, l3, l4) => l2 }).toList
  }

}
