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


# help for usage of the script
if [ "${1}" == "-h" ];
then
  echo "Usage: $(basename "${0}") {SPARK_MODE} \
  {INPUT_DIR} {OUTPUT_DIR}"
  exit 0
fi

# checking if the number of args to the script are as expected
if [ $# -lt 3 ]
then
  echo "Missing Operand"
  echo "Run $(basename "${0}") -h for usage"
  exit 0
fi

SPARK_MODE="${1}"
IN_DIR="${2}"
OUT_DIR="${3}"


echo "Values Being Used :- "
echo "SPARK_MODE - ${SPARK_MODE}"
echo "INPUT_DIR_OPTIONAL - ${IN_DIR}"
echo "OUTPUT_DIR_OPTIONAL - ${OUT_DIR}"

# Starting the spark application
spark-submit \
--class io.github.pratikbarhate.invertedindex.Main \
--master "${SPARK_MODE}" \
"${APP_HOME}"/lib/inverted-index-0.1-SNAPSHOT.jar \
"${APP_HOME}"/conf/output_conf.json \
"${APP_HOME}"/conf/spark_conf.json \
"${IN_DIR}" \
"${OUT_DIR}"
