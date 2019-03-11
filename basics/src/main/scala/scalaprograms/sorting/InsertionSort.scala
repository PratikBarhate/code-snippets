package scalaprograms.sorting

/**
  * This sorting technique is useful when we can make sure that the elements are nearly sorted.
  */
object InsertionSort extends Sort {

  def main(args: Array[String]): Unit = {
    val unsorted = Seq(1, 23, 4, 100, 6, 8, 94, 2)
    val sorted = sort(unsorted)
    sorted foreach println
  }

  private def insertElement[T](xs: Seq[T], element: T)(ord: Ordering[T]): Seq[T] = {
    xs match {
      case Nil => Seq(element)
      case head :: tail if ord.gt(head, element) => element :: head :: tail
      case head :: tail => head +: insertElement(tail, element)(ord)
    }
  }

  /**
    * Insertion sort.
    *
    * @param in  The unsorted [[Seq]] of objects.
    * @param ord The [[Ordering]] type required, default is ASC.
    * @tparam T The type of the objects in the input sequence.
    * @return sorted [[Seq]]
    */
  def sort[T](in: Seq[T])(implicit ord: Ordering[T]): Seq[T] = {
    in.foldLeft(Seq.empty[T])((acc, element) => insertElement(acc, element)(ord))
  }
}
