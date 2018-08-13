package io.github.pratikbarhate.spark.udafs

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

import scala.collection.JavaConverters._

/**
  * `SimpleMedian`
  *
  * Apache Spark UDAF to get the median of a column of NumericType.
  *
  * 1. Sorting is in ascending order.
  * 2. UPDATE: The update method is based on insert operation of insertion sort.
  * 3. MERGE: The merge method is based on merge operation of merge sort.
  * Initial result is kept as parameter which is `Nil`(An empty List)
  *
  * It can used with `agg`function along with `groupBy` function as most probably
  * the number of rows per group may fit within the limit.
  *
  * NOTE: The UDAF will fail for data with large number of rows where,
  * Number of Rows > 2147483646. IT WILL FAIL.While using with `groupBy`
  * the "Number of Rows" refer to rows per group (for each group).
  * Hence, named "Simple" median function.
  *
  *
  * FUTURE SCOPE:
  * Write a median function which would work on distributed dataset.
  *
  */
class SimpleMedian extends UserDefinedAggregateFunction {

  private def insert(xs: List[Double], element: Double): List[Double] = {
    xs match {
      case Nil => List(element)
      case head :: tail if head > element => element :: head :: tail
      case head :: tail => head :: insert(tail, element)
    }
  }

  private def merge(xs1: List[Double], xs2: List[Double], result: List[Double]): List[Double] = {
    (xs1, xs2) match {
      case (h1 :: t1, h2 :: _) if h1 < h2 => h1 :: merge(t1, xs2, result)
      case (h1 :: _, h2 :: t2) if h1 > h2 => h2 :: merge(xs1, t2, result)
      case (l1, Nil) => result ++ l1
      case (Nil, l2) => result ++ l2
    }
  }

  override def inputSchema: StructType = StringType(Array(
    StructField(name = "current_value",
      dataType = DoubleType,
      nullable = false)
  ))

  override def bufferSchema: StructType = StructType(Array(
    StructField("values", ArrayType(elementType = DoubleType, containsNull = false), nullable = false),
    StructField("num_of_elements", dataType = IntegerType, nullable = false)
  ))

  override def dataType: DataType = DoubleType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = new Array[Double](0)
    buffer(1) = 0
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = insert(buffer.getList[Double](0).asScala.toList, input.getDouble(0))
    buffer(1) = buffer.getInt(1) + 1
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = merge(buffer1.getList[Double](0).asScala.toList,
      buffer2.getList[Double](0).asScala.toList,
      result = Nil)
    buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
  }

  override def evaluate(buffer: Row): Double = {
    val n = buffer.getInt(1)
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
