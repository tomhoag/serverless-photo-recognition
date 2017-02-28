package com.squarepi


import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.elasticache.*
import com.amazonaws.services.elasticache.model.*

import com.squarepi.pojo.LabelCountItem

import com.budilov.Properties

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
	private var host: String
	private var port: Int
	
	private val jedis: Jedis

	init {
		
		//host = "redis-19727.c8.us-east-1-4.ec2.cloud.redislabs.com"
		//port = 19727

		host = "redis-19727.c8.us-east-1-4.ec2.cloud.redislabs.com"
		port = 19727
		
		println("jedis: " + host + ":" + port)

		jedis = Jedis(host,port); 
		jedis.connect() 		
		jedis.incr("restarts")
		println("jedis restarts: " + jedis.get("restarts"))
	}
	
	fun config(): String {
		return host + ":" + port
	}

	fun add(userId: String, labels: List<String>) { 

		for(label in labels) {	
			jedis.hincrBy(userId, label, 1)
		}	
	}
	
	fun getLabelCount(userId: String): List<LabelCountItem> {
		val map = jedis.hgetAll(userId)
		
		var list = mutableListOf<LabelCountItem>()
		for ((key, value) in map)
		{
			list.add(LabelCountItem(key, value))
		}
		return list
	}
	

}