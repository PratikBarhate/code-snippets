package spark

/*
 * Dataset used - [https://archive.ics.uci.edu/ml/datasets/adult]
 * Dataset is named `adult_train` and `adult_test` in folder "src/main/resources/dataset"
 */

import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.{Column, Encoders, SparkSession}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.OneHotEncoder
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.functions.{trim, when, length, col}

object LogisticReg {

  case class Schema(age: Double,
                    workclass: String,
                    fnlwgt: Double,
                    education: String,
                    education_num: Double,
                    marital_status: String,
                    occupation: String,
                    relationship: String,
                    race: String,
                    sex: String,
                    capital_gain: Double,
                    capital_loss: Double,
                    hours_per_week: Double,
                    native_country: String,
                    income: String)

  def main(args: Array[String]): Unit = {


    val spark = SparkSession.builder().master("local").appName("Databricks_Adult").getOrCreate()

    def emptyToNull(c: Column) = when(length(trim(c)) > 0, c)

    // define data schema to be loaded
    val dataSchema = Encoders.product[Schema].schema
    // load the data
    // change the load file path as per the system in use

    val trainDataDf = spark.read.format("csv").option("header", "false").
      option("delimiter", ",").schema(dataSchema).
      load("/Users/tkmahxk/Pratik/Study/Projects/ml-snippets/scala/src/main/resources/dataset/adult_train.csv"). // file path as needed
      withColumn("workclass", emptyToNull(col("workclass"))).
      withColumn("education", emptyToNull(col("education"))).
      withColumn("marital_status", emptyToNull(col("marital_status"))).
      withColumn("occupation", emptyToNull(col("occupation"))).
      withColumn("relationship", emptyToNull(col("relationship"))).
      withColumn("race", emptyToNull(col("race"))).
      withColumn("sex", emptyToNull(col("sex"))).
      withColumn("native_country", emptyToNull(col("native_country"))).
      withColumn("income", emptyToNull(col("income"))).na.drop

    // change the load file path as per the system in use
    val testDataDf = spark.read.format("csv").option("header", "false").
      option("delimiter", ",").schema(dataSchema).
      load("/Users/tkmahxk/Pratik/Study/Projects/ml-snippets/scala/src/main/resources/dataset/adult_test.csv"). //file path as needed
      withColumn("workclass", emptyToNull(col("workclass"))).
      withColumn("education", emptyToNull(col("education"))).
      withColumn("marital_status", emptyToNull(col("marital_status"))).
      withColumn("occupation", emptyToNull(col("occupation"))).
      withColumn("relationship", emptyToNull(col("relationship"))).
      withColumn("race", emptyToNull(col("race"))).
      withColumn("sex", emptyToNull(col("sex"))).
      withColumn("native_country", emptyToNull(col("native_country"))).
      withColumn("income", emptyToNull(col("income"))).na.drop

    // Stages in Pipeline
    val stages: ListBuffer[Any] = new ListBuffer[Any]

    val categoricalColumns: List[String] = List("workclass", "education",
      "marital_status", "occupation", "relationship", "race", "sex", "native_country")


    categoricalColumns.foreach({
      x =>
        val stringIndexer = new StringIndexer().setInputCol(x).setOutputCol(x+"Index")
        val encoder = new OneHotEncoder().setInputCol(x+"Index").setOutputCol(x+"ClassVec")
        stages += (stringIndexer, encoder)
    })

    val labelIndexer = new StringIndexer().setInputCol("income").setOutputCol("label")
    stages += labelIndexer

    val numericColumns: List[String] = List("age", "fnlwgt", "education_num",
      "capital_gain", "capital_loss", "hours_per_week")

    val assemblerInputs = categoricalColumns.map(x => x+"ClassVec") ++ numericColumns

    val assembler = new VectorAssembler().setInputCols(assemblerInputs.toArray).setOutputCol("features")
    stages += assembler

    val pipeline = new Pipeline().setStages(
      Array(
        stages.head.asInstanceOf[StringIndexer], stages(1).asInstanceOf[OneHotEncoder],
        stages(2).asInstanceOf[StringIndexer], stages(3).asInstanceOf[OneHotEncoder],
        stages(4).asInstanceOf[StringIndexer], stages(5).asInstanceOf[OneHotEncoder],
        stages(6).asInstanceOf[StringIndexer], stages(7).asInstanceOf[OneHotEncoder],
        stages(8).asInstanceOf[StringIndexer], stages(9).asInstanceOf[OneHotEncoder],
        stages(10).asInstanceOf[StringIndexer], stages(11).asInstanceOf[OneHotEncoder],
        stages(12).asInstanceOf[StringIndexer], stages(13).asInstanceOf[OneHotEncoder],
        stages(14).asInstanceOf[StringIndexer], stages(15).asInstanceOf[OneHotEncoder],
        stages(16).asInstanceOf[StringIndexer], stages(17).asInstanceOf[VectorAssembler])
    )

    // Fit the pipeline
    val pipelineModel = pipeline.fit(trainDataDf)

    // transform the training data
    val allColumnsTrainDataDf = pipelineModel.transform(trainDataDf)
    // transform the testing data
    val allColumnsTestDataDf = pipelineModel.transform(testDataDf)

    // select only needed columns
    val trainingDf = allColumnsTrainDataDf.select("features", "label")
    val testingDf = allColumnsTestDataDf.select("features", "label")

    // training the model
    val logicReg = new LogisticRegression().setLabelCol("label").setFeaturesCol("features").setMaxIter(100)
    val logicRegModel = logicReg.fit(trainingDf)

    // predictions from the already trained model
    val logicRegPredictions = logicRegModel.transform(testingDf)
    logicRegPredictions.show
  }
}