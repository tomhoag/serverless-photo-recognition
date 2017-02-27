package com.squarepi

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.elasticache.*
import com.amazonaws.services.elasticache.model.*
//import com.budilov.Properties
import redis.clients.jedis.*


/**
 * Created by Tom Hoag on 2/26/17.
 *
 */

fun main(args: Array<String>) {
    
    val ec = ECLabelCountService()
    
    

	val list = listOf("Hello", "World", "!")
    ec.add("foo",list)
    
}

class ECLabelCountService {
	
	//private val ecService = AmazonElastiCacheClientBuilder.defaultClient()
	private val host: String
	private val port: Int
	
	private val jedis: Jedis

	init {

		//val accessKey = "<foo>"
		//val accessSecret = "<bar>"
		//val credentials = BasicAWSCredentials(accessKey, accessSecret)
		
		//val client = AmazonElastiCacheClient(credentials);
		val client = AmazonElastiCacheClient(EnvironmentVariableCredentialsProvider());
		
		var dccRequest = DescribeCacheClustersRequest();
		dccRequest.setShowCacheNodeInfo(true);
        //dccRequest.withCacheClusterId("spr-count-001")
        
		val clusterResult = client.describeCacheClusters(dccRequest);
		//println("clusterResult: " + clusterResult)
		
		val cacheClusters = clusterResult.getCacheClusters();

		for (cacheCluster in cacheClusters) {
			//println("cacheCluster: " + cacheCluster)
			var cacheNodes = cacheCluster.getCacheNodes();
			for(node in cacheNodes) {
				println("node: " + node.endpoint.address + ":" + node.endpoint.port )
			}
		}
		
		host = cacheClusters[0].cacheNodes[0].endpoint.address
		port = cacheClusters[0].cacheNodes[0].endpoint.port
		
		//host = "spr-count2.hcehne.ng.0001.use1.cache.amazonaws.com"
		//port = 6379
		
		//host = "localhost"
		//port = 6379
		println("jedis: " + host + ":" + port)

		jedis = Jedis(host,port); 

		jedis.connect() 


	}

	fun add(userId: String, labels: List<String>) {
		
		//println("host: " + host + " port: " + port)
		val jedis = Jedis(host,port);
 
		jedis.connect(); 
		
		for(label in labels) {
			val key = userId+"|"+label
			jedis.incr(key)	
		}	
	}
	

}