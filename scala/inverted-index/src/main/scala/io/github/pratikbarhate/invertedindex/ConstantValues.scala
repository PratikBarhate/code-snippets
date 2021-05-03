package io.github.pratikbarhate.invertedindex

import scala.util.matching.Regex

object ConstantValues {
  // String Constants
  val regexFileNameStr = "([^/]*)$"
  val regexWhiteSpacesStr = "\\s+"
  val regexNotAlphaNumericStr = "[^a-zA-Z0-9']"
  val SingleSpaceStr = " "
  val EmptyStr = ""

  //Regex Objects
  val regexFileName: Regex = regexFileNameStr.r
  val regexWhiteSpaces: Regex = regexWhiteSpacesStr.r
  val regexNotAlphaNumeric: Regex = regexNotAlphaNumericStr.r

  // Configuration constants
  val FILE_DICTIONARY_PARTS = "file_dictionary_parts"
  val WORD_DICTIONARY_PARTS = "word_dictionary_parts"
  val OUTPUT_INDEX_PARTS = "output_index_parts"
}
