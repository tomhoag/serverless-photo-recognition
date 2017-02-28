package com.squarepi

import com.squarepi.pojo.LabelCountItem

import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient
import com.amazonaws.services.cognitoidentity.model.GetIdRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

import com.budilov.pojo.ApigatewayRequest
import com.budilov.Properties

import com.google.gson.Gson

import java.util.*



/**
 * Created by Tom Hoag -- inspired by Vladimir Budilov
 *
 * This Lambda function allows clients to search the ElasticSearch index for photos with specific tags.
 *
 * The API Gateway requests need to be signed: https://docs.aws.amazon.com/apigateway/api-reference/signing-requests/
 *
 *
 */

class SearchPhotosHandler : RequestHandler<ApigatewayRequest.Input, com.squarepi.SearchPhotosHandler.SearchResponse> {

    private val ecService = ECLabelCountService()
    
    private val identityClient: AmazonCognitoIdentity = AmazonCognitoIdentityClient(AnonymousAWSCredentials())

    private val _RESPONSE_EMPTY = "Input parameters weren't there"

    data class SearchResponse(val statusCode: Int,
                              val headers: MutableMap<String, String>?,
                              val body: String)

    //data class ResponseBody(val message: String, val pictures: List<PictureItem>)
    data class ResponseBody(val message: String, val pictures: List<LabelCountItem>)
    
    /**
     * 1. Get the request from API Gateway. Unmarshal (automatically) the request
     * 2. Get the
     */
    override fun handleRequest(request: ApigatewayRequest.Input?, context: Context?): SearchResponse? {

        val logger = context?.logger

        logger?.log("request payload: " + Gson().toJson(request))

        if (request == null || context == null) {
            logger?.log("request or context is null")
        } else {
	        
			val labelList: List<LabelCountItem> = ecService.getLabelCount(getCognitoId(request.headers?.get("Authorization") ?: ""))
            logger?.log("Found labels: " + labelList)

            val headers: MutableMap<String, String> = HashMap()
            headers.put("Content-Type", "application/json")

            return SearchResponse(200, headers, Gson().toJson(ResponseBody("Success", labelList)))
        }
        return SearchResponse(400, null, """{"message":"$_RESPONSE_EMPTY"}""")
    }

    /**
     * Retrieve the cognito id from the cognito service
     *
     * The result should be cached so as not to call the cognito service for every single request (although I'm not
     * caching it anywhere right now)
     */
    fun getCognitoId(authToken: String): String {
	    
        val idRequest = GetIdRequest()
        idRequest.accountId = Properties._ACCOUNT_NUMBER
        idRequest.identityPoolId = Properties._COGNITO_POOL_ID
        idRequest.logins = mapOf(Pair(Properties._COGNITO_POOL_ID_IDP_NAME, authToken))
       
        val idResp = identityClient.getId(idRequest)

        return idResp.identityId ?: ""
    }
}