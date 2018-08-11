package io.github.pratikbarhate.spark.ml

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer, VectorAssembler}
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel, ParamGridBuilder}
import org.apache.spark.ml.{Pipeline, PipelineStage}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Encoders, SparkSession}
import CommonOps.emptyToNull

import scala.collection.mutable.ListBuffer

object LogisticReg {

  /**
    * Dataset used - [https://archive.ics.uci.edu/ml/datasets/adult]
    * Dataset is named `adult_train` and `adult_test` in folder "src/main/resources/dataset"
    * Spark default logs are blocked so that the logs printed on the console are less
    *
    * Logistic Regression to classify the income to be above or below 50K
    */

  // Removes spark logs that bloat the console.
  Logger.getLogger("akka").setLevel(Level.OFF)
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("breeze").setLevel(Level.OFF)

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

    val spark = SparkSession.builder().master("local[5]").appName("Databricks_Adult").getOrCreate()

    // define data schema to be loaded
    val dataSchema: StructType = Encoders.product[Schema].schema
    // load the data
    // change the load file path as per the system in use

    val trainDataDf = spark.read.format("csv").option("header", "false").
      option("delimiter", ",").schema(dataSchema).
      load("./src/main/resources/datasets/adult_train.csv"). // file path as needed
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
      load("./src/main/resources/datasets/adult_test.csv"). //file path as needed
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
    val stages: ListBuffer[PipelineStage] = new ListBuffer[PipelineStage]

    val categoricalColumns: List[String] = List("workclass", "education",
      "marital_status", "occupation", "relationship", "race", "sex", "native_country")


    categoricalColumns.foreach({
      x =>
        val stringIndexer = new StringIndexer().setInputCol(x).setOutputCol(x + "Index")
        val encoder = new OneHotEncoder().setInputCol(x + "Index").setOutputCol(x + "ClassVec")
        stages += (stringIndexer, encoder)
    })

    val labelIndexer = new StringIndexer().setInputCol("income").setOutputCol("label")
    stages += labelIndexer

    val numericColumns: List[String] = List("age", "fnlwgt", "education_num",
      "capital_gain", "capital_loss", "hours_per_week")

    val assemblerInputs = categoricalColumns.map(x => x + "ClassVec") ++ numericColumns

    val assembler: VectorAssembler = new VectorAssembler().setInputCols(assemblerInputs.toArray).setOutputCol("features")
    stages += assembler

    val pipeline = new Pipeline().setStages(stages.toArray)

    // Fit the pipeline
    val pipelineModel = pipeline.fit(trainDataDf)

    // transform the training data
    val allColumnsTrainDataDf = pipelineModel.transform(trainDataDf)
    // transform the testing data
    val allColumnsTestDataDf = pipelineModel.transform(testDataDf)

    // select only needed columns
    val trainingDf = allColumnsTrainDataDf.select("features", "label").cache
    trainDataDf.count
    val testingDf = allColumnsTestDataDf.select("features", "label")

    // training the model
    val logicReg = new LogisticRegression().setLabelCol("label").setFeaturesCol("features")

    val paramGrid = new ParamGridBuilder()
      .addGrid(logicReg.regParam, Array(0.1, 0.01, 0.3))
      .addGrid(logicReg.maxIter, Array(50, 100, 1000))
      .addGrid(logicReg.elasticNetParam, Array(0.5, 0.8, 0.3))
      .addGrid(logicReg.tol, Array(1E-3, 1E-6, 1E-12))
      .build()

    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("f1")

    val cv: CrossValidator = new CrossValidator()
      .setEstimator(logicReg)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(2)
      .setParallelism(2)
      .setCollectSubModels(true)

    val cvModel: CrossValidatorModel = cv.fit(trainingDf)

    cvModel.write.save("./src/main/resources/trainedModel")

    val loadedModel: CrossValidatorModel = CrossValidatorModel.load("./src/main/resources/trainedModel")

    val result = loadedModel.transform(testingDf)

    result.show(false)
  }
}