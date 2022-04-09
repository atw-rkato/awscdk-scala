package com.myorg.example

import com.myorg.lib.{MyStack, StackContext, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.services.s3.{BlockPublicAccess, Bucket, ObjectOwnership}

object SampleS3Stack extends StackFactory {
  val id: StackId = StackId("s3-stack")

  def apply()(implicit ctx: StackContext): SampleS3Stack = {
    implicit val stack: MyStack = MyStack(this.id)

    val s3Bucket: Bucket = Bucket.Builder
      .create(stack, "SampleS3Bucket")
      .bucketName("my-sample-s3-bucket-ap-northeast-1")
      .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
      .removalPolicy(RemovalPolicy.DESTROY)
      .objectOwnership(ObjectOwnership.BUCKET_OWNER_ENFORCED)
      .build()

    SampleS3Stack(s3Bucket)
  }
}

case class SampleS3Stack private (s3Bucket: Bucket)(implicit stack: MyStack) extends StackWrapper(stack)
