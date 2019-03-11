package scalaprograms.sorting

/**
  * This trait is helpful in defining any implicits required for sorting.
  * e.g. the [[Ordering]] object for custom classes, whenever used.
  *
  * The algorithms are implemented with Functional Programming paradigm in mind.
  */
trait Sort {
  def sort[T](in: Seq[T])(implicit ord: Ordering[T]): Seq[T]
}
