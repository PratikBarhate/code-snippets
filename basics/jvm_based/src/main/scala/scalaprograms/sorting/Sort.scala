package scalaprograms.sorting

/**
  * This trait is helpful in defining any implicits required for sorting.
  * e.g. the [[Ordering]] object for custom classes, whenever used.
  */
trait Sort {
  /**
    * Sorting method.
    * Preserves the original [[Seq]] while
    * returning the new sorted [[Seq]].
    *
    * @param in  The unsorted [[Seq]] of objects.
    * @param ord The [[Ordering]] type required, default is ASC.
    * @tparam T The type of the objects in the input sequence.
    * @return sorted [[Seq]]
    */
  def sort[T](in: Seq[T])(implicit ord: Ordering[T]): Seq[T]
}
