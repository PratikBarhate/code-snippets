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

# check if the file path is provided
if [ "${1}" == "" ]
then
    echo "File path is missing. Please provide the file path"
    exit -1
fi
# check if the word to be searched is provided
if [ "${2}" == "" ]
then
    echo "Please provide the word to be searched"
    exit -1
fi

MERGED_FILES_DIR=${APP_HOME}/merged_files

# execute the search operation on the given file
java -cp ${APP_HOME}/lib/SplitSortAndSearch-1.0.jar org.maverick.scala.core.SearchWord ${1} ${2} ${MERGED_FILES_DIR}

if [ ${?} != 0 ]
then
    echo "ERROR :: In Searching, exiting the code...."
    exit 1
fi
