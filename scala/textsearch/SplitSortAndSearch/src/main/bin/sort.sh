#!/bin/bash

#Find the script file home
pushd . > /dev/null
SCRIPT_DIRECTORY="${BASH_SOURCE[0]}";
while([ -h "${SCRIPT_DIRECTORY}" ]);
do
  cd "`dirname "${SCRIPT_DIRECTORY}"`"
  SCRIPT_DIRECTORY="$(readlink "`basename "${SCRIPT_DIRECTORY}"`")";
done
cd "`dirname "${SCRIPT_DIRECTORY}"`" > /dev/null
SCRIPT_DIRECTORY="`pwd`";
popd  > /dev/null
APP_HOME="`dirname "${SCRIPT_DIRECTORY}"`"


# run the SplitAndSort Class
# generate timestamp for current execution
TIMESTAMP=$(date +%s%N)
# check if the file path is provided
if [ "${1}" == "" ]
then
    echo "File path is missing. Please provide the file path"
    exit -1
fi
# create tmp directory if not exists
if [ ! -d "${APP_HOME}/tmp" ]
then
    mkdir ${APP_HOME}/tmp
fi
# set the current temp directory
CURRENT_TMP=${APP_HOME}/tmp/${TIMESTAMP}
# if the tmp directory with the same timestamp exists (if created externally by other means then delete it)
if [ -d "${CURRENT_TMP}" ]
then
    rm -rf ${CURRENT_TMP}
fi
# create a directory with timestamp to store the current execution data
mkdir ${CURRENT_TMP}

# execute the split operation
java -Dconfig.file=${APP_HOME}/etc/application.conf \
-cp ${APP_HOME}/lib/SplitSortAndSearch-1.0.jar org.maverick.scala.core.LogicalSplit ${1} ${CURRENT_TMP}

if [ ${?} != 0 ]
then
    echo "ERROR :: In Spliting, exiting the code...."
    # remove the tmp directory for current execution
    rm -rf ${CURRENT_TMP}
    exit 1
fi

# created directory to store sorted files of current execution
if [ -d "${CURRENT_TMP}/sorted" ]
then
    rm -rf ${CURRENT_TMP}/sorted
fi
mkdir ${CURRENT_TMP}/sorted

# execute the sort operation
java -cp ${APP_HOME}/lib/SplitSortAndSearch-1.0.jar \
org.maverick.scala.core.LogicalSplitSorting ${1} ${CURRENT_TMP} ${CURRENT_TMP}/sorted

if [ ${?} != 0 ]
then
    echo "ERROR :: In Sorting, exiting the code...."
    # remove the tmp directory for current execution
    rm -rf ${CURRENT_TMP}
    exit 1
fi

# create directory to store the merged files
if [ ! -d "${APP_HOME}/merged_files" ]
then
    mkdir ${APP_HOME}/merged_files
fi

# find the number of files to know the number of partitions
NUMBER_OF_PARTITIONS=$(find ${CURRENT_TMP}/sorted -type f | wc -l)

# execute merging of sorted files for current execution
java -cp ${APP_HOME}/lib/SplitSortAndSearch-1.0.jar \
org.maverick.scala.core.MergeSortedFiles ${CURRENT_TMP}/sorted ${APP_HOME}/merged_files ${NUMBER_OF_PARTITIONS} ${1}

if [ ${?} != 0 ]
then
    echo "ERROR :: In Sorting, exiting the code...."
    # remove the tmp directory for current execution
    rm -rf ${CURRENT_TMP}
    exit 1
fi

# remove the tmp directory for current execution
rm -rf ${CURRENT_TMP}