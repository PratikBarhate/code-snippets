package scalaprograms.sorting

/**
  * 1. This sorting technique has a worst time complexity of O(n * log n).
  * 2. Stable sort.
  *
  * * One disadvantage of this sorting technique is that
  * * it creates two array objects for each merge operation.
  * * Line number 38.
  */
object MergeSort extends Sort {

  def main(args: Array[String]): Unit = {
    val unsorted = Seq(1, 23, 4, 100, 6, 8, 94, 2)
    println(s"Original order of elements: $unsorted")
    // ordering defines the sorting to be in descending order.
    val sorted = sort(unsorted)(Ordering.Int.reverse)
    println(s"Sorted order of elements: $sorted")
  }

  /**
    * This implementation create one extra object for each call
    * to [[merge()]] method for returning the result,
    * than the imperative code in Java.
    */
  private def merge[T](xs1: Seq[T], xs2: Seq[T])(ord: Ordering[T]): Seq[T] = {
    (xs1, xs2) match {
      case (l1, Nil) => l1
      case (Nil, l2) => l2
      case (h1 +: t1, h2 +: _) if ord.lt(h1, h2) => h1 +: merge(t1, xs2)(ord)
      case (h1 +: _, h2 +: t2) if ord.gt(h1, h2) => h2 +: merge(xs1, t2)(ord)
    }
  }

  override def sort[T](in: Seq[T])(implicit ord: Ordering[T]): Seq[T] = {
    val n = in.length / 2
    if (n == 0) in
    else {
      val (left, right) = in splitAt n
      merge(sort(left)(ord), sort(right)(ord))(ord)
    }
  }
}
