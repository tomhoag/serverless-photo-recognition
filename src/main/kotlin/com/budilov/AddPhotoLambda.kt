package com.budilov

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
//import com.budilov.db.ESPictureService
import com.squarepi.ECLabelCountService
import com.budilov.pojo.PictureItem
import com.budilov.rekognition.RekognitionService
import java.net.URLDecoder

/**
 * Created by Vladimir Budilov
 *
 * This Lambda function is invoked by S3 whenever an object is added to an S3 bucket.
 */
class AddPhotoLambda : RequestHandler<S3Event, String> {

    private val rekognition = RekognitionService()
    //private val esService = ESPictureService()
    private val ecService = ECLabelCountService()

    /**
     * 1. Get the s3 bucket and object name in question
     * 2. Clean the object name
     * 3. Run the RekognitionService service to get the labels
     * 4. Save the bucket/object & labels into ElasticSearch
     */
    override fun handleRequest(s3event: S3Event, context: Context): String {
        val record = s3event.records.first()
        val logger = context.logger
        val srcBucket = record.s3.bucket.name

        // Object key may have spaces or unicode non-ASCII characters.
        var srcKeyEncoded = record.s3.`object`.key
                .replace('+', ' ')

        val srcKey = URLDecoder.decode(srcKeyEncoded, "UTF-8")
        logger.log("bucket: ${srcBucket}, key: ${srcKey}")

        // Get the cognito id from the object name (it's a prefix)
        val cognitoId = srcKey.split("/")[1]

        val labels = rekognition.getLabels(srcBucket, srcKey)
                
        if (labels.isNotEmpty()) {
            //val picture = PictureItem(srcKeyEncoded.hashCode().toString(), srcBucket + Properties._BUCKET_URL + "/" + srcKey, labels, null)
            //logger.log("Saving picture: ${picture}")
            // Save the picture to ElasticSearch
            // esService.add(cognitoId, picture)
            
            // Save the picture labels to Elaticache
            ecService.add(cognitoId, labels)

        } else {
            logger.log("No labels returned. Not saving to ES or EC")
            //todo: create an actionable event to replay the flow
        }
        
        val map = ecService.getLabelCount(cognitoId)
                
		for ((key, value) in map)
		{
			System.out.println(key + ":" + value);
		}

        return "Ok"
    }

}