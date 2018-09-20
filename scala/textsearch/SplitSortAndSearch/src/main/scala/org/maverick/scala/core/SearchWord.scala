package org.maverick.scala.core

import java.math.BigInteger
import java.security.MessageDigest

import scala.util.control.Breaks._

import org.maverick.scala.util._

object SearchWord {

  def main(args: Array[String]): Unit = {

    def binarySearch(list: List[String], key: String, low: Int, high: Int): Int = {
      val mid = (high + low)/2
      // word not found
      if(low > high) return -1
      if(key.compareTo(list(mid)) == 0)
         mid
      else{
        if(key.compareTo(list(mid)) < 0) binarySearch(list,key,low,mid - 1)
        else binarySearch(list,key,mid + 1, high)
      }
    }

    // get filePath
    val filePath = args(0)
    //get the word to be searched
    val key = args(1)
    // get the merged files directory
    val mergeFilesDir = args(2)

    //-------------------------READ THE CO-RESPONDING MERGED DATA FILE--------------------------------------------------
    val mergedFileName: String = String.format("%064x",new BigInteger(1,MessageDigest.getInstance("SHA-256").
      digest(filePath.getBytes("UTF-8"))))

    val sortedData: Option[List[String]] = FileOperations.readFile(mergeFilesDir+"/"+mergedFileName)
    if(sortedData.isEmpty){
      print("\nRun `bin/sort.sh file_path` on the given file first, for efficient searching (Binary search) ")
      sys.exit(1)
    }

    //----------------------------------SEARCH THE GIVEN KEY------------------------------------------------------------
    val lenghtOfSortedData = sortedData.get.length
    val index: Int = binarySearch(sortedData.get,key,0,lenghtOfSortedData -1 )
    //look for the repeated occurrences
    // we found the number at least ounce
    if(index >= 0){
      var count = 1
      var l = index - 1
      var h = index + 1
      if(l >= 0){
        breakable{
          while(sortedData.get(l).compareTo(key) == 0 && l >= 0 ){
            l = l - 1
            count = count + 1
            if( l < 0) break()
          }
        }
      }
      if(h < lenghtOfSortedData){
        breakable{
          while(sortedData.get(h).compareTo(key) == 0 && h < lenghtOfSortedData){
            h = h + 1
            count = count + 1
            if(h >= lenghtOfSortedData) break()
          }
        }
      }
      print("\n\n-----------------------------------------------RESULT------------------------------------------------")
      print("\nWord :: " + key + "\nPresent :: Yes\nNumber of times :: " + count)
    }
    else{
      print("\n\n-----------------------------------------------RESULT------------------------------------------------")
      print("\nWord :: " + key + "\nPresent :: No\nNumber of times :: " + 0)
    }
    // end of if statement to check word at least exists ounce
    // for better view of end of program on terminal
    print("\n")
    sys.exit(0)
  }
}
