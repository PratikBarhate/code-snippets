package io.github.pratikbarhate.invertedindex

class ProcessTextFilesTest extends TestBase {

  "The file name " should " be extracted " +
    "from the absolute path " in {
    val filePath = "file://test_path/directory_test/file1.txt"
    val fileContent = "Some random text"
    val privateMethod = PrivateMethod[(String, Array[String])]('cleanRawData)
    val (name, _) = ProcessTextFiles
      .invokePrivate(privateMethod(filePath, fileContent))
    val expectedText = "file1.txt"
    name.shouldEqual(expectedText)
  }

  "A file with white space characters" should " be cleaned with at max " +
    "one space between words " in {
    val filePath = "/some/absolute/path"
    val fileContent = "To, Test    the \n  white \t spaces"
    val privateMethod = PrivateMethod[(String, Array[String])]('cleanRawData)
    val (_, text: Array[String]) = ProcessTextFiles
      .invokePrivate(privateMethod(filePath, fileContent))
    val expectedText = Array("To", "Test", "the", "white", "spaces")
    text.shouldEqual(expectedText)
  }

  "A file with special characters" should " be cleaned, not removing the " +
    "apostrophe within words" in {
    val filePath = "/some/absolute/path"
    val fileContent = "Test!! with@# special% characters, don't remove ' ?"
    val privateMethod = PrivateMethod[(String, Array[String])]('cleanRawData)
    val (_, text: Array[String]) = ProcessTextFiles
      .invokePrivate(privateMethod(filePath, fileContent))
    val expectedText =
      Array("Test", "with", "special", "characters", "don't", "remove")
    text.shouldEqual(expectedText)
  }

  "For the given files the method " should " return correct inverted " +
    "index " in {
    val data = List(
      ("/file0", "f0w1 f0w2 cw1 cw2"),
      ("/file1", "f1w1 f1w2 cw1 cw2")
    )
    val testRdd = sc.parallelize(data)
    val (fi, wi, indexes) = ProcessTextFiles.process(testRdd)
    val fiArr: Array[String] = fi.collect()
    val wiArr: Array[String] = wi.collect()
    val indexArr: Array[String] = indexes.collect()

    println(s"Input: ${data.mkString(" :: ")}")
    println(s"File Dictionary: ${fiArr.toSeq.mkString(" :: ")}")
    println(s"Word Dictionary: ${wiArr.toSeq.mkString(" :: ")}")
    println(s"Output Index: ${indexArr.toSeq.mkString(",")}")

    fiArr.shouldEqual(Array("file0,0", "file1,1"))
    wiArr.shouldEqual(
      Array("f0w2,0", "f1w2,1", "cw1,2", "f0w1,3", "cw2,4", "f1w1,5")
    )
    indexArr.shouldEqual(
      Array("0:[0]", "1:[1]", "2:[0,1]", "3:[0]", "4:[0,1]", "5:[1]")
    )
  }

}
