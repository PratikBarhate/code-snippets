package io.github.pratikbarhate.invertedindex

import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.json4s._
import org.json4s.jackson.JsonMethods._

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      throw new IllegalArgumentException(
        "Output files configuration, Spark configuration file, " +
          "input directory and output directory is expected." +
          s"\nProvide: ${args.toList}"
      )
    }

    implicit val jsonFormat: DefaultFormats.type = DefaultFormats
    val outputConfigs = parse(IOUtils.readLocalFile(args(0)))
      .extract[Map[String, String]]
    val sparkConfigs = parse(IOUtils.readLocalFile(args(1)))
      .extract[Map[String, String]]
    val outputDir = args(3)
    val fileDictParts = outputConfigs(ConstantValues.FILE_DICTIONARY_PARTS).toInt
    val wordDictParts = outputConfigs(ConstantValues.WORD_DICTIONARY_PARTS).toInt
    val outputIndexParts = outputConfigs(ConstantValues.OUTPUT_INDEX_PARTS).toInt

    val sparkConf = new SparkConf()
      .setAppName("inverted-index")
      .setAll(sparkConfigs)

    implicit val sc: SparkContext = new spark.SparkContext(sparkConf)

    val rawRdd = sc.wholeTextFiles(args(2))
    val (fileRdd, wordRdd, indexRdd) =
      ProcessTextFiles.process(rawRdd)

    fileRdd
      .coalesce(fileDictParts, shuffle = false)
      .saveAsTextFile(
        IOUtils.getFileDictionaryPath(outputDir)
      )
    wordRdd
      .coalesce(wordDictParts, shuffle = false)
      .saveAsTextFile(
        IOUtils.getWordDictionaryPath(outputDir)
      )
    indexRdd
      .coalesce(outputIndexParts, shuffle = false)
      .saveAsTextFile(
        IOUtils.getOutputPath(outputDir)
      )
  }
}
