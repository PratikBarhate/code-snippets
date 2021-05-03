package io.github.pratikbarhate.invertedindex

import scala.io.Source

object IOUtils {
  def readLocalFile(path: String): String = {
    val bufferedSource = Source.fromFile(path)
    val fileStr = bufferedSource.getLines().mkString
    bufferedSource.close()
    fileStr
  }

  def getFileDictionaryPath(outputDir: String): String =
    s"$outputDir/intermediate_dictionaries/file_dictionary/"

  def getWordDictionaryPath(outputDir: String): String =
    s"$outputDir/intermediate_dictionaries/word_dictionary/"

  def getOutputPath(outputDir: String): String =
    s"$outputDir/output_inverted_index/"
}
