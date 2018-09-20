package org.maverick.scala.core

import org.maverick.scala.util._

import com.typesafe.config.{Config, ConfigFactory}

object LogicalSplit{

  def main(args: Array[String]): Unit = {

    //load the application.conf file
    val config: Config = ConfigFactory.load()
    //get the fileName from the user as a argument
    val originalFileName = args(0)
    //directory to store current tmp data
    val tmpDir = args(1)
    //get the number of partitions `N` from the PROJECT_MODULE/etc/application.conf
    val N: Int = config.getInt("partitions.N")
    print("\nNumber of partitions used, for sorting the file -> " + N)

    //------------------------------GET NUMBER OF LINES OF ORIGINAL FILE------------------------------------------------
    val numberOfLinesInFile: Option[Int] = FileOperations.getNumberOfLines(originalFileName)
    // end the program if number of lines cannot be fetched
    if(numberOfLinesInFile.isEmpty) sys.exit(1)

    //----------------------------LOGICAL PARTITIONING------------------------------------------------------------------
    //Logically partitioning the file in N parts on the basis of the number of lines
    val partitionData: String = if(numberOfLinesInFile.get < N){
      val partitionStep = 1
      val numberOfPartitions = numberOfLinesInFile
      var partitionData: String = ""
      for(i <- 0 until numberOfPartitions.get){
        // we want new line character at the beginning of every partition data so as to put it on separate lines
        // only first line doesn't need the new line character at the beginning
        if(i == 0){
          partitionData = partitionData + s"0 ${partitionStep*(i+1) -1}"
        }else{
          partitionData = partitionData + s"\n${partitionStep*i} ${partitionStep*(i+1) -1}"
        }
      }
      partitionData
    }
    else{
      val partitionStep:Int = numberOfLinesInFile.get/N
      val remainder: Int = numberOfLinesInFile.get%N
      var partitionData = ""
      // to balance the load first m = remainder partitions will have one extra line
      for(i <- 0 until remainder){
        // we want new line character at the beginning of every partition data so as to put it on separate lines
        // only first line doesn't need the new line character at the beginning
        if(i == 0){
          partitionData = partitionData + s"0 ${partitionStep*(i+1)}"
        }else{
          partitionData = partitionData + s"\n${partitionStep*i + i} ${partitionStep*(i+1) + i}"
        }
      }
      // now we have to set the new start for the remaining partitions
      val new_start = partitionStep * remainder + remainder
      for(i <- 0 until N - remainder){
        partitionData = partitionData + s"\n${new_start + partitionStep*i} ${new_start + partitionStep*(i+1) -1}"
      }
      partitionData
    }
    // end of partition logic

    //-----------------------WRITING PARTITION DATA TO `split-info.txt` FILE--------------------------------------------
    val splitInfoWriteStatus = FileOperations.writeFile(tmpDir+"/split-info.txt",partitionData)
    if(splitInfoWriteStatus){
      print("\nWriting partition data to `split-info.text` done ")
    }
    else{
      print("\nError in writing to `split-info.txt`......exiting the program")
      sys.exit(1)
    }
    // for clear view of end on terminal
    sys.exit(0)
  }
}