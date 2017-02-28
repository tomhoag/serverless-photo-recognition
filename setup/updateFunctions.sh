#!/usr/bin/env bash

if [ -z ${1+x} ]; then 
	echo "need a ROOT_NAME"; 
	exit -1
else 
	ROOT_NAME=$1; 
fi

REGION=us-east-1

# Lambda
JAR_LOCATION=../build/libs/rekognition-rest-1.0-SNAPSHOT.jar
FUNCTION_REK_SEARCH=rekognition-search-picture-${ROOT_NAME}
FUNCTION_REK_ADD=rekognition-add-picture-${ROOT_NAME}
FUNCTION_REK_DEL=rekognition-del-picture-${ROOT_NAME}

updateFunction() {
   if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]; then
     echo "No parameters were passed"
     exit -1
   fi

    aws lambda update-function-code \
    --region $1 \
    --function-name $2 \
    --zip-file fileb://$3
}

# Build the code again and update all of the lambda functions
cd ..
./gradlew clean
./gradlew build
cd setup
updateFunction ${REGION} ${FUNCTION_REK_ADD} ${JAR_LOCATION}
updateFunction ${REGION} ${FUNCTION_REK_DEL} ${JAR_LOCATION}
updateFunction ${REGION} ${FUNCTION_REK_SEARCH} ${JAR_LOCATION}