package io.github.pratikbarhate.spark.udafs

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
  * `CollectMap`
  *
  * This an example of an UDAF (User defined aggregate function) in apache spark
  * which generates a map of [String -> Double] from two columns.
  *
  * Analogous to collect_list function, this UDAF would create a map from two given columns,
  * first column being the keys and second[NumericType] column being the values.
  */
class CollectMap extends UserDefinedAggregateFunction {

  override def inputSchema: StructType = StructType(Array(
    StructField("key", StringType, nullable = false),
    StructField("value", DoubleType, nullable = false)))

  override def bufferSchema: StructType = StructType(Array(
    StructField("keys", ArrayType(elementType = StringType, containsNull = false)),
    StructField("values", ArrayType(elementType = DoubleType, containsNull = false))
  ))

  override def dataType: DataType =
    MapType(keyType = StringType,
      valueType = DoubleType,
      valueContainsNull = false)

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = new Array[String](0)
    buffer(1) = new Array[Double](0)
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = Seq(input.getString(0)) ++ buffer.getSeq[String](0)
    buffer(1) = Seq(input.getDouble(1)) ++ buffer.getSeq[Double](1)
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getSeq[String](0) ++ buffer2.getSeq[String](0)
    buffer1(1) = buffer1.getSeq[Double](1) ++ buffer2.getSeq[Double](1)
  }

  override def evaluate(buffer: Row): Map[String, Double] = {
    Map(buffer.getSeq[String](0).zip(buffer.getSeq[Double](1)): _ *)
  }

}
