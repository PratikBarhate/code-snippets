package org.maverick.scala.util

import java.io.{File, FileNotFoundException, IOException, PrintWriter}

import scala.io.BufferedSource
import scala.io.Source.fromFile

object FileOperations {

  //-----------------------------METHOD TO GET NUMBER OF LINES IN A FILE------------------------------------------------
  def getNumberOfLines(fileName: String): Option[Int] = {
    try{
      print("\nReading file from the source " + fileName + "......")
      val bufferedSource = fromFile(fileName)
      // Note that getLines returns an Iterator[String] so you aren't actually reading the whole file into to memory
      val numberOfLines = bufferedSource.getLines().size
      print("done")
      bufferedSource.close()
      Option(numberOfLines)
    }
    catch {
      case fe: FileNotFoundException =>
        print("\nERROR WHILE READING FILE ::" + fe.getMessage)
        None
      case ioe: IOException =>
        print("\nERROR WHILE READING FILE ::" + ioe.getMessage)
        None
      case e: Exception =>
        print("\nERROR WHILE READING FILE ::" + e.getMessage)
        None
    }
  }
  // end of method to count number of lines in a file

  //-----------------------------METHOD TO READ FROM A FILE-------------------------------------------------------------
  def readFile(fileName: String):Option[List[String]] = {
    try{
      print("\nReading file from the source " + fileName + "......")
      val bufferedSource = fromFile(fileName)
      val lines = bufferedSource.getLines().toList
      print("done")
      bufferedSource.close()
      Option(lines)
    }
    catch {
      case fe: FileNotFoundException =>
        print("\nERROR WHILE READING FILE ::" + fe.getMessage)
        None
      case ioe: IOException =>
        print("\nERROR WHILE READING FILE ::" + ioe.getMessage)
        None
      case e: Exception =>
        print("\nERROR WHILE READING FILE ::" + e.getMessage)
        None
    }
  }
  //end of readFile method

  //-----------------------------METHOD TO GET FILE BUFFERED READER-----------------------------------------------------
  def getFileIter(fileName: String):Option[BufferedSource] = {
    try{
      print("\nGetting buffered reader for the file, from the source " + fileName + "......")
      val bufferedSource = fromFile(fileName)
      print("done")
      Option(bufferedSource)
    }
    catch {
      case fe: FileNotFoundException =>
        print("\nERROR WHILE READING FILE ::" + fe.getMessage)
        None
      case ioe: IOException =>
        print("\nERROR WHILE READING FILE ::" + ioe.getMessage)
        None
      case e: Exception =>
        print("\nERROR WHILE READING FILE ::" + e.getMessage)
        None
    }
  }
  //end of readFile method

  //---------------------------------METHOD TO WRITE DATA TO FILE-------------------------------------------------------
  def writeFile(filePath: String, dataToWrite: String): Boolean = {
    val file = new File(filePath)
    val fileCreatedStatus = try{
      val splitFileCreatedStatus: Boolean = file.createNewFile()
      if(splitFileCreatedStatus){
        print("\nFile created :: " + filePath)
      }
      else {
        print("Cannot create file :: " + filePath)
      }
      splitFileCreatedStatus
    }catch {
      case ioe: IOException =>
        print("ERROR IS CREATING :: " + filePath + "\n" + ioe.getMessage)
        false
    }
    //id split file cannot be created then exit with status 1
    val writeStatus: Boolean = if(!fileCreatedStatus){
      fileCreatedStatus
    }
    else {
      val fileWriter = new PrintWriter(file)
      fileWriter.write(dataToWrite)
      fileWriter.close()
      true
    }
    writeStatus
  }
  // end of writeFile method
}
