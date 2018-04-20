package spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.LDA
import org.apache.spark.ml.feature.{CountVectorizer, NGram, StopWordsRemover, Tokenizer}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import spark.CommonOps._

/**
  * We are trying to cluster the Health-tweets data from UCI dataset list.
  * Data link - [https://archive.ics.uci.edu/ml/datasets/Health+News+in+Twitter]
  * Spark default logs are blocked so that the logs printed on the console are less
  *
  * Latent Dirichlet's Allocation algorithm is used.
  *
  *
  * Operators Required:
  * 1. Tokenizer or RegexTokenizer (for more powerful tokenize operation)
  * 2. StopWordsRemover
  * 3. N-Gram sequence of tokens
  * 4. CountVectorizer
  * 5. regex operations to extract from raw text data, sequence of regex operations.
  * 6. Lemmatizer from "Johnsnowlabs" or "StanfordCoreNLP" NLP library.
  *
  * LDA Models Params:
  * 1. Topic concentration / Beta
  * 2. Document concentration / Alpha
  * 3. K = number of topics we are expecting
  * 4. Optimizer - "em" or "online" only these two are supported, currently in Spark v2.3.0
  *
  * NOTE:
  * -->| Intuition on the values of Alpha and Beta :-
  * For the symmetric distribution, a high alpha-value means that each document is likely to
  * contain a mixture of most of the topics, and not any single topic specifically.
  * A low alpha value puts less such constraints on documents and means that it is more likely
  * that a document may contain mixture of just a few, or even only one, of the topics.
  * Likewise, a high beta-value means that each topic is likely to contain a mixture of most of the words,
  * and not any word specifically, while a low value means that a topic may contain a mixture of just
  * a few of the words.
  *
  * If, on the other hand, the distribution is asymmetric, a high alpha-value means that a specific
  * topic distribution (depending on the base measure) is more likely for each document.
  * Similarly, high beta-values means each topic is more likely to contain a specific
  * word mix defined by the base measure.
  *
  * In practice, a high alpha-value will lead to documents being more similar in terms
  * of what topics they contain. A high beta-value will similarly lead to topics being
  * more similar in terms of what words they contain.
  *
  */

object LDADocClustering {

  // Removes spark logs that bloat the console.
  Logger.getLogger("akka").setLevel(Level.OFF)
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("breeze").setLevel(Level.OFF)

  // Change the path according to where data is stored or pass the path as the argument.
  private val baseDataPath = "./src/main/resources/datasets/Health-Tweets"
  private val keyColName = "file"
  private val rawDataColName = "data"
  private val dataColName = "tweets"
  private val featureVecColName = "features_vec"

  /**
    * Custom UDF for parsing raw text file and
    * extract required data.
    * i) Removed all the web hyperlinks
    * ii) Removed all the non-alphanumeric characters
    * iii) Removed all the extra white spaces
    *
    * @return [String] Tweets all combined to form a single document
    */
  private def parseRawFile: UserDefinedFunction =
    udf((rawData: String) => {
      val tweetsCombined = rawData.split(EndLineChar).map(x => x.split('|')(2)).mkString(SingleWhiteSpace)
      webLinksRegex.replaceAllIn(extraWhiteSpaceRegex.replaceAllIn(
        nonAlphaNumericRegex.replaceAllIn(tweetsCombined, EmptyString), EmptyString), EmptyString)
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

  def main(args: Array[String]): Unit = {

    /**
      * Data path can be provided externally.
      */
    val dataPath = if (args.length == 1) args(0) else baseDataPath

    val sparkSession = SparkSession.builder()
      .master("local[6]")
      .appName("lda_clustering")
      .getOrCreate()

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
    val nGramStage = new NGram().setN(3).setInputCol("removed_stop_words").setOutputCol("n_gram_output")
    val countVectorizer = new CountVectorizer().setInputCol("n_gram_output").setOutputCol(featureVecColName)
      .setMinDF(2)

    /**
      * We need to load the data.
      * Set Custom data transformations to get the raw data to expected schema.
      */
    val docsWithRawDataDF: DataFrame = sparkSession.sparkContext.wholeTextFiles(dataPath)
      .toDF(keyColName, rawDataColName)
    val docsWithParsedDataDF: DataFrame = docsWithRawDataDF.withColumn(dataColName, parseRawFile(col(rawDataColName)))

    /**
      * ->| Transformation
      * 1. Remove stop-words from the documents.
      * 2. Transform data as expected by the LDA clustering algorithm.
      *
      * ->| Model parameters
      * 1. Cluster the documents into `k = 5` topics using LDA.
      * 2. Value of `k` can be optimized by calculating "Kullback Leibler Divergence Score".
      */
    val cleanedDataDF = stopWordsRemover.transform(tokenizer.transform(docsWithParsedDataDF))
    val nGramWordSeqDF = nGramStage.transform(cleanedDataDF)
    val countVectorizerModel = countVectorizer.fit(nGramWordSeqDF)
    // Final feature DataFrame we get after all the transformation
    val featureDF = countVectorizerModel.transform(nGramWordSeqDF)
    println(s"Data preparation transformations defined.")
    // Initialize the LDA clustering model class
    val lda = new LDA().setK(5).setMaxIter(100).setFeaturesCol(featureVecColName)
    val lDAModel = lda.fit(featureDF)

    val ll = lDAModel.logLikelihood(featureDF)
    val lp = lDAModel.logPerplexity(featureDF)
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
    val transformed = lDAModel.transform(featureDF)
    transformed.printSchema()
    transformed.select("file", "topicDistribution").show(truncate = false)
  }
}
