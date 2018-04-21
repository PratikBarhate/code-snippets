package spark

import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{length, trim, when}

import scala.util.matching.Regex

object CommonOps {

  val extraWhiteSpaceRegex: Regex = "\\s+".r
  val nonAlphaNumericWithSpaceRegex: Regex = "[^0-9a-zA-Z\\s]".r
  val webLinksRegex: Regex = "(http://[^\\s]*)|(www\\.[^\\s]*)".r
  val EmptyString: String = ""
  val SingleWhiteSpace: String = " "
  val EndLineChar: Char = '\n'

  def emptyToNull(c: Column): Column = when(length(trim(c)) > 0, c)
}
