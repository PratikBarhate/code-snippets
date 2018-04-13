package spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.LDA
import org.apache.spark.ml.feature.{CountVectorizer, StopWordsRemover, Tokenizer}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import spark.CommonOps._

object LDADocClustering {

  /**
    * We are trying to cluster the Health-tweets data from UCI dataset list.
    * Data link - [https://archive.ics.uci.edu/ml/datasets/Health+News+in+Twitter]
    * Spark default logs are blocked so that the logs printed on the console are less
    *
    * Latent Dirichlet's Allocation algorithm is used.
    *
    */

  // Removes spark logs that bloat the console.
  Logger.getLogger("akka").setLevel(Level.OFF)
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("breeze").setLevel(Level.OFF)

  // Change this according to the ath where data is stored.
  private val basePath = "/Users/tkmah7q/Pratik/Study/Projects/ml-snippets/scala/src/main/resources/datasets/Health-Tweets"
  private val keyColName = "file"
  private val rawDataColName = "data"
  private val dataColName = "tweets"
  private val featureVecColName = "features_vec"

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder().master("local[6]").appName("Databricks_Adult").getOrCreate()
    import sparkSession.implicits._

    /**
      * 1. Set the transformer required for the given data with correct column names
      * 2. RegexTokenizer can be used for complex tokenize logic, where separators can be a regex expression.
      * 3. Remove stop words from the
      * 3. We set the minDF = 1, that means a word will be included in the vocabulary
      * when it appears in minimum of 1 document.
      */
    val tokenizer = new Tokenizer().setInputCol(dataColName).setOutputCol("tokenize")
    val stopWordsRemover = new StopWordsRemover().setInputCol("tokenize").setOutputCol("removed_stop_words")
    val countVectorizer = new CountVectorizer().setInputCol("removed_stop_words").setOutputCol(featureVecColName)
      .setMinDF(1)

    /**
      * We need to load the data.
      * Set Custom data transformations to get the raw data to expected schema
      */
    val docsWithRawDataDF: DataFrame = sparkSession.sparkContext.wholeTextFiles(basePath)
      .toDF(keyColName, rawDataColName)
    val docsWithParsedDataDF: DataFrame = docsWithRawDataDF.withColumn(dataColName, parseRawFile(col(rawDataColName)))

    /**
      * ->| Transformation
      * 1. Remove stop-words from the documents.
      * 2. Transform data as expected by the LDA clustering algorithm.
      *
      * ->| Model parameters
      * 1. Cluster the documents into `k = 16` topics using LDA.
      * 2. Value of `k` can be optimized by calculating "Kullback Leibler Divergence Score".
      */
    val cleanedDataDF = stopWordsRemover.transform(tokenizer.transform(docsWithParsedDataDF))
    val countVectorizerModel = countVectorizer.fit(cleanedDataDF)
    val featueDF = countVectorizerModel.transform(cleanedDataDF)
    // Initialize the LDA clustering model class
    val lda = new LDA().setK(16).setMaxIter(100).setFeaturesCol(featureVecColName)
    val lDAModel = lda.fit(featueDF)

    val ll = lDAModel.logLikelihood(featueDF)
    val lp = lDAModel.logPerplexity(featueDF)
    println(s"The lower bound on the log likelihood of the entire corpus: $ll")
    println(s"The upper bound on perplexity: $lp")

    // Describe topics.
    val topics = lDAModel.describeTopics(3)
    println("The topics described by their top-weighted terms:")
    val countVecVocabulary: List[String] = countVectorizerModel.vocabulary.toList
    val topicWithWords: DataFrame = topics
      .withColumn("key_words", getWordsFromCountVecMode(col("termIndices"), typedLit(countVecVocabulary)))
    topicWithWords.show(false)

    // Shows the result.
    val transformed = lDAModel.transform(featueDF)
    transformed.printSchema()
    transformed.select("file", "topicDistribution").show(truncate = false)

  }

  /**
    * Custom UDF for parsing raw text file and
    * extract required data.
    *
    * @return [String] Tweets all combined to form a single document
    */
  private def parseRawFile: UserDefinedFunction =
    udf((rawData: String) => {
      val tweetsCombined = rawData.split(EndLineChar).map(x => x.split('|')(2)).mkString(SingleWhiteSpace)
      extraWhiteSpaceRegex.replaceAllIn(
        notAlphaNumericRegex.replaceAllIn(tweetsCombined, SingleWhiteSpace), SingleWhiteSpace)
    })

  /**
    * Custom UDF to get the words from the CountVectorizerModel
    * given the indices so that we can understand the topics.
    *
    * @return [[Seq]] of Strings representing the topic
    */
  private def getWordsFromCountVecMode: UserDefinedFunction =
    udf((indices: Seq[Int], vocabulary: Seq[String]) => {
      indices.map(x => vocabulary(x))
    })
}
