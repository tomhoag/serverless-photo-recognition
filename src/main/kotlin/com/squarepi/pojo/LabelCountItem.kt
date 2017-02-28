package com.squarepi.pojo


/**
 * Created by Tom Hoag
 *
 */
// data class PictureItem(@JestId val id: String, val s3BucketUrl: String, val labels: List<String>?, var signedUrl: String?)

data class LabelCountItem(val label: String, val count: String)
