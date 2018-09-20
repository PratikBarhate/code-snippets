package org.maverick.scala.core

import akka.actor.Actor
import org.maverick.scala.util.FileOperations

import scala.io.BufferedSource
import scala.util.{Sorting, Try}

class SortingActor extends Actor {
  //------------------------------ACTOR CLASS FOR SORTING A SINGLE PARTITION--------------------------------------------

  //------------------------------METHOD TO SORT AND WRITE THE WORDS TO FILE--------------------------------------------
  def sortingNPartition(data: List[String], partitionNumber: Int, sortedFilesDir: String): Unit = {

    val words: Array[String] = data.mkString(" ").split("\\s")
    // clean the data of non-alpha-numeric members
    val removed_symbols : Array[String] = words.map(w => w.replaceAll("[^A-Za-z0-9]",""))
    val clean_words: Array[String] = removed_symbols.filter(x => x != "")
    // cleaning done
    Sorting.quickSort(clean_words)
    val sortedWords: String = clean_words.mkString("\n")
    val writeSortStatus: Boolean = FileOperations.writeFile(sortedFilesDir + s"/sorted-$partitionNumber.txt",sortedWords)
    if(writeSortStatus){
      print("\nSorting done for partition :: " + partitionNumber)
    }else{
      print("\nError occured while storing sorted data for partition :: " + partitionNumber)
    }
  }

  def receive: PartialFunction[Any, Unit] = {
    case message: String =>
      val messageInfo: Array[String] = message.split("\\s")
      if(messageInfo.length == 4){
        val partitionNumber: Try[Int]= Try(messageInfo(2).toInt)
        if(partitionNumber.isSuccess){

          //--------------------GET THE `split-info.txt` FILE---------------------------------------------------------
          val splitInfoBuffer: Option[BufferedSource] = FileOperations.getFileIter(messageInfo(1))
          //end if split-info cannot be fetched
          if(splitInfoBuffer.isEmpty){
            sender ! "Something went wrong, in creating `split-info.txt` process(LogicalSplit)"
            context.stop(self)
          }
          val splitInfoIter: Iterator[String] = splitInfoBuffer.get.getLines()

          //---------------------GET THE ORIGINAL FILE(for which we are computing)------------------------------------
          val originalFileBuffer: Option[BufferedSource] = FileOperations.getFileIter(messageInfo(0))
          //end if original file cannot be fetched
          if(originalFileBuffer.isEmpty){
            sender ! "Invalid file path"
            context.stop(self)
          }
          val originalFileIter: Iterator[String] = originalFileBuffer.get.getLines()

          //-----------------------------SORT THE GIVEN PARTITION OF THE FILE-----------------------------------------
          // skip the first n - 1 lines to get the split-info of Nth partition
          val splitInfoForNthPartiton: String = if(partitionNumber.get == 0) splitInfoIter.next()
          else splitInfoIter.drop(partitionNumber.get - 1).next()
          // each line has start and end line for the given partition
          val splitData: Array[String] = splitInfoForNthPartiton.split("\\s")
          //slice the required lines fo the file for which we are sorting
          val lines: List[String] = originalFileIter.slice(splitData(0).toInt,splitData(1).toInt + 1).toList
          sortingNPartition(lines,partitionNumber.get,messageInfo(3))
          //close the opened file
          originalFileBuffer.get.close()
          splitInfoBuffer.get.close()
          print("\nCompleted sorting, file partition number :: " + partitionNumber.get )
          sender ! "Success"
          context.stop(self)
        }
        else{
          sender ! "Invalid partition number"
          context.stop(self)
        }
      }
      else{
        sender ! "Invalid message"
        context.stop(self)
      }
    // end of case message: String

    case _ =>
      sender ! "Wrong Input Datatype expected String"
      context.stop(self)
  }
}
