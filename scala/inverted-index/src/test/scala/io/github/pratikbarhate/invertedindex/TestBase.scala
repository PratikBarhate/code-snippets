package io.github.pratikbarhate.invertedindex

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.PrivateMethodTester

trait TestBase extends AnyFlatSpec with Matchers with PrivateMethodTester {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

  private val conf =
    new SparkConf()
      .setAppName("test-cloudflare-takehome")
      .setMaster("local")
  protected val sc: SparkContext = new SparkContext(conf)
}
