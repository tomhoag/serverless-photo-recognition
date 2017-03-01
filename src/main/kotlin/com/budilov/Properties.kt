package com.budilov

/**
 * Created by Vladimir Budilov
 *
 * Externalizing of app properties. Will be handy when writing unit tests and de-coupling
 * the storage of properties
 */

object Properties {
    val _REGION = "us-east-1"
    val _ACCOUNT_NUMBER = "266942040790"
    val _BUCKET_NAME = "rekognition-20170228123539"
    val _BUCKET_URL = "." + _BUCKET_NAME + "-" + _REGION + ".amazonaws.com"
    val _ES_SERVICE_URL = "https://"
    val _REKOGNITION_URL = "https://rekognition." + _REGION + ".amazonaws.com"
    val _REKOGNITION_CONFIDENCE_THRESHOLD = 60
    val _COGNITO_POOL_ID = "us-east-1:e74be541-677b-4ace-8054-0a934d9c549d"
    val _USER_POOL_ID = "us-east-1_OeAshXqhs"
    val _COGNITO_POOL_ID_IDP_NAME = "cognito-idp." + _REGION + ".amazonaws.com/" + _USER_POOL_ID
    val _S3_SIGNED_URL_DURATION = 1
}

