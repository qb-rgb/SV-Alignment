package fasta

/** Object that extract a sequence from a FASTA file.
  *
  * @author Quentin Baert
  */
object FASTAReader {

  import scala.io.Source

  /** Read a sequence from a FASTA file.
    *
    * @param fileName file name of the FASTA file
    * @return sequence extract from the FASTA file
    */
  def readFrom(fileName: String): String = {
    val source = Source fromFile fileName
    val lines = try source.getLines.toList finally source.close

    (lines.tail filterNot { _ == "\n" }).mkString
  }

}
