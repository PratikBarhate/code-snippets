package io.github.pratikbarhate.invertedindex

import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.RDD

object ProcessTextFiles {

  /**
   * Method to process the raw text files, and create
   * the inverted index structure.
   * Steps:-
   * 1. Clean the text to remove special characters and white spaces.
   * Along with that extra the file name from the absolute path.
   * Process the step using `mapPartitions` API so that regex objects
   * are fetched once per partition.
   * 2. Persist the data as `zipWithIndex` creates spark job, and we can avoid
   * processing the same data again. Use [[StorageLevel.MEMORY_AND_DISK]] so
   * that the data is spilled to disk in case of constrained main memory
   * 3. `zipWithIndex` gives an index to the file-names.
   * 4. Map all the words of file to the file-index
   * 6. Keep the words and file-index combination unique.
   * 7. Group all the file-index together by the word, using `aggregateByKey`
   * interface. As we can sort the file-index using insertion sort logic during
   * the aggregation phase, removing the need for one more iteration of sorting.
   * 8. Sort the file indexes
   * 9. Again persist the processes data to avoid re-computation.
   * 10. `zipWithIndex` gives indexes to words. The indexes are already in order
   * according to the assigned index, hence no need to do explicit sorting.
   * 11. Make the data into [[RDD]] of [[String]] and return the processes
   * information.
   *
   * @param rdd [[RDD]] from reading raw text files.
   * @return [[Tuple3]] of [[RDD]] of [[String]]
   */
  def process(
    rdd: RDD[(String, String)]
  ): (RDD[String], RDD[String], RDD[String]) = {
    val cleanRdd: RDD[((String, Array[String]), Long)] =
      rdd
        .mapPartitions(rows =>
          rows.map({ case (file, text) => cleanRawData(file, text) })
        )
        .persist(StorageLevel.MEMORY_AND_DISK)
        .zipWithIndex()

    val wordsRdd: RDD[((String, Seq[Long]), Long)] = cleanRdd
      .flatMap({ case ((_, words), fi) => words.map(x => (x, fi)) })
      .distinct
      .groupByKey()
      .map({ case (word, fi) => (word, fi.toSeq.sorted) })
      .persist(StorageLevel.MEMORY_AND_DISK)
      .zipWithIndex()

    (
      cleanRdd
        .map({ case ((f, _), fi) => s"$f,$fi" }),
      wordsRdd
        .map({ case ((w, _), wi) => s"$w,$wi" }),
      wordsRdd
        .map({ case ((_, fis), wi) => s"$wi:${fis.mkString("[", ",", "]")}" })
    )
  }

  /**
   * Method to get the file name and clean the raw text.
   * Steps:-
   * 1. Extract the file name from the absolute path
   * 2. Split the text data around character which are not aplha-numeric.
   * Keeping the apostrophe between words.
   * 3. Remove empty strings
   * 4. Remove single apostrophe
   *
   * @param absolutePath Absolute path of the text file
   * @param text          Contents of the text file
   * @return [[Tuple2]] with first element as [[String]] and
   *        second as [[Array]] of [[String]]
   */
  private def cleanRawData(
    absolutePath: String,
    text: String
  ): (String, Array[String]) = {
    val fileName = ConstantValues.regexFileName.findFirstIn(absolutePath).get
    val cleanText = ConstantValues.regexNotAlphaNumeric
      .split(text)
      .filterNot(w => w.isEmpty || w.equals("'"))
    (fileName, cleanText)
  }

}
