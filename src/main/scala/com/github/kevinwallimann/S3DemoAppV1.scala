
package com.github.kevinwallimann

import com.github.kevinwallimann.MyAwsSdkApp.S3DemoAppConfig
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ListObjectsRequest

object S3DemoAppV1 {
  def execute(config: S3DemoAppConfig): Unit = {
    val s3 = AmazonS3ClientBuilder.standard().build()
    val response = s3.listObjects(new ListObjectsRequest()
      .withBucketName(config.bucket)
    )
    import scala.collection.JavaConverters._
    val objects = response.getObjectSummaries.asScala
    objects.foreach(s3Object => println(s3Object.toString))
  }
}
