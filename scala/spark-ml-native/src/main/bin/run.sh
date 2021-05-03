#!/bin/bash

# Find the script file home
pushd . > /dev/null
SCRIPT_DIRECTORY="${BASH_SOURCE[0]}";
while [ -h "${SCRIPT_DIRECTORY}" ];
do
  cd "$(dirname "${SCRIPT_DIRECTORY}")" || exit
  SCRIPT_DIRECTORY="$(readlink "$(basename "${SCRIPT_DIRECTORY}")")";
done
cd "$(dirname "${SCRIPT_DIRECTORY}")" > /dev/null || exit
SCRIPT_DIRECTORY="$(pwd)";
popd  > /dev/null || exit
APP_HOME="$(dirname "${SCRIPT_DIRECTORY}")"

# checking if the number of args to the script are proper
if [ $# != 2 ]
then
  echo "Missing Operand"
  echo "Total of 2 arguments are expected"
  echo "{NUM_OF_ITER} {MATRIX_SIZE}"
  exit 0
fi

echo "Using default JVM implementation"
spark-submit --master local \
"${APP_HOME}"/lib/default-0.1-SNAPSHOT.jar "${1}" "${2}" &
PID="${!}"

psrecord "${PID}" --plot mm_"${1}"_"${2}"_default.png

# Sleep for 2 minutes so that all the processes close completely
sleep 2m

# Skip one line
echo ""
echo "Using native BLAS implementation"
spark-submit --master local \
"${APP_HOME}"/lib/native-0.1-SNAPSHOT.jar "${1}" "${2}" &
PID="${!}"

psrecord "${PID}" --plot mm_"${1}"_"${2}"_native.png