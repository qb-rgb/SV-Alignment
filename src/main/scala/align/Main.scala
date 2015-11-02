package align

import extension._
import fasta._
import indexation._
import search._

object MainAlign {

  type OptionMap = Map[String, String]

  private val errorLog = "ERROR"

  private def catchOptions(args: Array[String]): OptionMap = {
    def nextOption(map: OptionMap, args: List[String]): OptionMap = args match {
      case "-genome" :: value :: tail => nextOption(map ++ Map("-genome" -> value), tail)

      case "-reads" :: value :: tail => nextOption(map ++ Map("-reads" -> value), tail)

      case "-seedlength" :: value :: tail => nextOption(map ++ Map("-seedlength" -> value), tail)

      case "-seednb" :: value :: tail => nextOption(map ++ Map("-seednb" -> value), tail)

      case "-match" :: value :: tail => nextOption(map ++ Map("-match" -> value), tail)

      case "-mismatch" :: value :: tail => nextOption(map ++ Map("-mismatch" -> value), tail)

      case "-indel" :: value :: tail => nextOption(map ++ Map("-indel" -> value), tail)

      case "-purcent" :: value :: tail => nextOption(map ++ Map("-purcent" -> value), tail)

      case Nil => map

      case _ => Map("error" -> this.errorLog)
    }

    nextOption(Map(), args.toList)
  }

  // Check if all the arguments are correct
  private def checkOptions(map: OptionMap): Boolean =
    !(map.values.toList contains this.errorLog)

  def main(args: Array[String]): Unit = {
    val options = this.catchOptions(args)

    if (this.checkOptions(options)) {
      val genome = FASTAReader readFrom options("-genome")
      val reads = FASTQReader readFrom options("-reads")
      val seedlength = options("-seedlength").toInt
      val matchScore = options("-match").toInt
      val mismatch = options("-mismatch").toInt
      val indel = options("-indel").toInt
      val purcent = options("-purcent").toInt
      val seedExtracter =
        if (options("-seednb") == "max")
          MaximalSeedExtracter
        else
          MinimalSeedExtracter

      val indexer = new SuffixTableIndexer(genome)
      val seeker = new ExactSeeker(indexer)
      val extender = new Extender(genome, seeker, matchScore, mismatch, indel, 20, purcent)

      for {
        read <- reads
        (seed, pos) <- seedExtracter.extractSeedsFrom(read, seedlength)
        alignment <- extender.extend(read, seed, pos)
      } {
        println()
        println(alignment)
        println()
      }
    } else
      sys.exit(1)
  }

}
