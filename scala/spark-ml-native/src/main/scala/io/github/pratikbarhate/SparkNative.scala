package io.github.pratikbarhate

import org.apache.spark.ml.linalg.DenseMatrix

object SparkNative {

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      throw new IllegalArgumentException(
        "Two arguments are expected {NUM_OF_ITER} {MATRIX_SIZE}" +
          s"\nProvide: ${args.toList}"
      )
    }

    val numOfIter = args(0).toInt
    val matSize = args(1).toInt

    val left = DenseMatrix.rand(matSize, matSize, new java.util.Random)
    val right = DenseMatrix.rand(matSize, matSize, new java.util.Random)
    val (_, timeInMilli) = timedBlock(for (_ <- 0 until numOfIter) {
      left multiply right
    })

    val totalSeconds = timeInMilli / math.pow(10, 3)

    println(
      "\nMatrix Multiplication :: " +
        s"\nNumber of iterations: $numOfIter" +
        s"\nMatrix size: $matSize" +
        s"\nTotal time: $totalSeconds seconds\n"
    )
  }

  /**
   * Method take an execution block and returns the time
   * required to execute the block of code in milliseconds,
   * along the return statement of the executed block.
   *
   * WARNING: Make sure you include the action on the
   * execution block, without an action Apache Spark
   * will continue building an execution graph and
   * actual execution time won't be clocked.
   *
   * @param block The block of code to be timed
   * @tparam R Return type of the given execution block
   * @return [[Tuple2]] of ([[R]], [[Long]])
   */
  def timedBlock[R](block: => R): (R, Long) = {
    val start = System.currentTimeMillis()
    val result = block
    val end = System.currentTimeMillis()
    val totalMilli = end - start
    val milliSeconds = totalMilli
    (result, milliSeconds)
  }

}