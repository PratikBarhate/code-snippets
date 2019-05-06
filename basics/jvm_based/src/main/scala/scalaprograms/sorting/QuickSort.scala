package scalaprograms.sorting

/**
  * One of the most efficient sorting algorithm with average and best
  * time complexity of O(n * log n) and wort complexity of O(square(n)).
  *
  * NOTE:
  * Do not use the this implementation, as it will hog your RAM
  * with large number of object creation.
  */
object QuickSort extends Sort {

  def main(args: Array[String]): Unit = {
    val unsorted = Seq(1, 23, 4, 100, 6, 8, 94, 2)
    println(s"Original order of elements: $unsorted")
    val sorted = sort(unsorted)
    println(s"Sorted order of elements: $sorted")
  }

  override def sort[T](in: Seq[T])(implicit ord: Ordering[T]): Seq[T] = {
    if (in.length < 2) in
    else {
      val pivotIndex = scala.util.Random.nextInt(in.size)
      val pivotValue = in(pivotIndex)
      val (left, right) = in.patch(pivotIndex, Nil, 1).partition(x => ord.lt(x, pivotValue))
      (sort(left) :+ pivotValue) ++ sort(right)
    }
  }
}
