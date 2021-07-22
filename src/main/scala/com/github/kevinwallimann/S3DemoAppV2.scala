
package com.github.kevinwallimann

import com.github.kevinwallimann.MyAwsSdkApp.S3DemoAppConfig
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ListObjectsRequest

object S3DemoAppV2 {
  def execute(config: S3DemoAppConfig): Unit = {
    val s3Client: S3Client = S3Client.builder().build()
    val listObjectsRequest = ListObjectsRequest.builder().bucket(config.bucket).build()
    val response = s3Client.listObjects(listObjectsRequest)
    import scala.collection.JavaConverters._
    val objects = response.contents().asScala
    objects.foreach(s3Object => println(s3Object.toString))
  }
}
