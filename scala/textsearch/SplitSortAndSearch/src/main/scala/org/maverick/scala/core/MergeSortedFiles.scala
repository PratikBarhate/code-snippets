package org.maverick.scala.core

import java.io.File

import org.maverick.scala.util._
import java.security.MessageDigest
import java.math.BigInteger

import scala.collection.mutable.ArrayBuffer

object MergeSortedFiles {

  def main(args: Array[String]): Unit = {

    // filePath for sorted files
    val sortedFilesDir = args(0)
    // filePath to store merged data
    val mergedFileDir = args(1)
    // number of partitions used while sorting
    val numberOfPartions = args(2).toInt
    //get original file path(name)
    val originalFilePath = args(3)

    // create ListBuffer to hold the data from all the files
    val allData: ArrayBuffer[String] = new ArrayBuffer[String]

    //------------------READ ALL THE SORTED FILES AND MERGE THEM--------------------------------------------------------
    for(i <- 0 until numberOfPartions){
      val tmp: ArrayBuffer[String] = allData.clone()
      allData.clear()
      val words: Option[List[String]] = FileOperations.readFile(s"$sortedFilesDir/sorted-$i.txt")
      val lengthOfMerged = tmp.length
      val lenghtOfNew = words.get.length
      var m: Int = 0
      var n: Int = 0
      while(m < lengthOfMerged && n < lenghtOfNew ){
        if(tmp(m).compareTo(words.get(n)) < 0){
          allData.append(tmp(m))
          m = m + 1
        }
        else{
          allData.append(words.get(n))
          n = n + 1
        }
      }
      // if the elements of original data are remaining
      while(m < lengthOfMerged){
        allData.append(tmp(m))
        m = m + 1
      }
      // if elements of new files are remaining
      while(n < lenghtOfNew ){
        allData.append(words.get(n))
        n = n + 1
      }
    }

    //--------------------WRITE THE MERGED DATA TO FILE-----------------------------------------------------------------
    val mergedFileName: String = String.format("%064x",new BigInteger(1,MessageDigest.getInstance("SHA-256").
      digest(originalFilePath.getBytes("UTF-8"))))
    print("\nMERGED_FILE_NAME=" + mergedFileName)
    //if the merged file already exist ( we want to re-create the data on same file, as the file has been modified)
    val mergeFile = new File(mergedFileDir+s"/$mergedFileName")
    if(mergeFile.exists())mergeFile.delete()
    //finally write the merged data to the unique file
    val writeStatusMerge = FileOperations.writeFile(mergedFileDir+s"/$mergedFileName", allData.mkString("\n"))
    if(writeStatusMerge){
      print("\nWriting the merged sorted data done")
    }
    else{
      print("\nERROR in writing the merged sorted data......")
      sys.exit(1)
    }
    // for better view of end on terminal
    print("\n")
    sys.exit(0)
  }
}
