package io.github.pratikbarhate.spark.udafs

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
  * DISCLAIMER: Work in progress.
  *
  * Trying to solve bottle-neck for type conversions from Scala types to
  * Spark's internal types (Catalyst converter).
  */
class DistMedian extends UserDefinedAggregateFunction {
  private val CurrentValue = "current_value"
  private val Values = "values"
  private val NumOfElements = "num_of_elements"
  private val NumOfMaps = "num_of_maps"

  override def inputSchema: StructType = StringType(Array(
    StructField(name = CurrentValue, dataType = DoubleType, nullable = false)
  ))

  override def bufferSchema: StructType = StructType(Array(
    StructField(Values,
      MapType(keyType = LongType,
        valueType = ArrayType(elementType = DoubleType, containsNull = false),
        valueContainsNull = false)
    ),
    StructField(NumOfElements, dataType = LongType, nullable = false),
    StructField(NumOfMaps, dataType = LongType, nullable = false)
  ))

  override def dataType: DataType = DoubleType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = collection.mutable.Map(0L -> DistMedian.emptyArray)
    buffer(1) = 0L
    buffer(2) = 1L
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = DistMedian.insertElement(buffer.getSeq[Double](0), input.getDouble(0))
    buffer(1) = buffer.getInt(1) + 1
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = DistMedian.mergeElements(buffer1.getSeq[Double](0),
      buffer2.getSeq[Double](0))
    buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
  }

  override def evaluate(buffer: Row): Double = {
    val n = buffer.getInt(1) - 1
    val values = buffer.getList[Double](0)
    if (n % 2 == 0) {
      values.get(n / 2)
    } else {
      val l = (n - 1) / 2
      val h = l + 1
      (values.get(l) + values.get(h)) / 2.0
    }
  }
}

private object DistMedian {
  val emptyArray = new Array[Double](0)

  def insertElement(xs: Seq[Double], element: Double): Seq[Double] = {
    xs match {
      case Nil => Seq(element)
      case head :: tail if head > element => element :: head :: tail
      case head :: tail => head +: insertElement(tail, element)
    }
  }

  def mergeElements(xs1: Seq[Double], xs2: Seq[Double]): Seq[Double] = {
    (xs1, xs2) match {
      case (h1 +: t1, h2 +: _) if h1 < h2 => h1 +: mergeElements(t1, xs2)
      case (h1 +: _, h2 +: t2) if h1 > h2 => h2 +: mergeElements(xs1, t2)
      case (l1, Nil) => l1
      case (Nil, l2) => l2
    }
  }
}
