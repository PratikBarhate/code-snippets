package org.maverick.scala.core

import org.maverick.scala.util._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.io.BufferedSource
import scala.util.{Sorting, Try}

object LogicalSplitSorting{

  //-----------------------MAIN METHOD OF THE OBJECT `LogicalSplitSorting`----------------------------------------------
  def main(args: Array[String]): Unit = {

    //get the fileName from the user as a argument
    val originalFilePath = args(0)
    //get the tmp directory for current execution
    val tmpDir = args(1)
    // get `split-info.txt` file path
    val splitFilePath = tmpDir + "/split-info.txt"
    // get sorted files directory
    val sortedFileDir = args(2)

    //---------------------------------INITIATE THE ACTOR SYSTEM--------------------------------------------------------
    val sortingSystem = ActorSystem("PartFiles-Sorting-System")
    implicit val timeout: Timeout = Timeout(1000.seconds)

    //---------------------------CREATING `N` ACTORS FOR EACH PARTITION-------------------------------------------------
    //creating ListBuffer to hold all the ActorRef and another ListBuffer to hold their responses
    val actorList: ListBuffer[ActorRef] = new ListBuffer[ActorRef]
    val futureList: ListBuffer[Future[Any]] = new ListBuffer[Future[Any]]
    val numberOfPartitions: Option[Int] = FileOperations.getNumberOfLines(splitFilePath)
    if(numberOfPartitions.isEmpty){
      print("\nERROR in getting number of lines of `split-info.txt`, in class `LogicalSplitSorting`")
      sys.exit(1)
    }

    for(i <- 0 until numberOfPartitions.get){
      actorList.append(sortingSystem.actorOf(Props[SortingActor]))
      futureList.append(actorList(i) ? s"$originalFilePath $splitFilePath $i $sortedFileDir")
    }

    for(i <- 0 until numberOfPartitions.get){
      val result: String = Await.result(futureList(i),timeout.duration).asInstanceOf[String]
      print(s"\nPartion $i status :: $result")
    }

    //-------------------------------------EXIT-------------------------------------------------------------------------
    print("\n")
    sys.exit(0)
  }
}
