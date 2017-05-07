#!/usr/bin/env bash

JAVA_RUN_DIR="/var/run/java"

jar_file="$1"
wait_for="$2"
wait_after="$3"

#echo "JAVA_DEBUG=${JAVA_DEBUG}"
if [ ! -z "${JAVA_DEBUG}" ]; then
	DEBUGGING_FLAGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=4000 "
fi

if [ ! -z "${wait_for}" ]; then
	/setup/wait-for-it.sh -t 180 "${wait_for}"
fi

if [ ! -z "${wait_after}" ]; then
        echo "Sleeping for additional ${wait_after} second(s)"
        sleep "${wait_after}"
fi

if [ ! -d "${JAVA_RUN_DIR}" ]; then
  mkdir -p "${JAVA_RUN_DIR}"
fi

cd /code
cp "/code/${jar_file}" "${JAVA_RUN_DIR}"
COMMAND="java ${DEBUGGING_FLAGS}-Duser.timezone=UTC -jar ${JAVA_RUN_DIR}/${jar_file}"

echo ""
java -version
echo ""
echo "Running jar file ${jar_file}:"
echo ""
echo ">   ${COMMAND}"
echo ""
${COMMAND}
