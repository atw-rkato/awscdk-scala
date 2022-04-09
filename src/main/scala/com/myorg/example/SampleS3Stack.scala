package com.myorg.example

import com.myorg.lib.{MyStack, StackArgs, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.services.s3.{BlockPublicAccess, Bucket, ObjectOwnership}

object SampleS3Stack extends StackFactory {
  val id: StackId = StackId("s3-stack")

  def apply(stackArgs: StackArgs): SampleS3Stack = {
    val stack = MyStack(this.id, stackArgs)
    val s3Bucket: Bucket = Bucket.Builder
      .create(stack, "SampleS3Bucket")
      .bucketName("my-sample-s3-bucket-ap-northeast-1")
      .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
      .removalPolicy(RemovalPolicy.DESTROY)
      .objectOwnership(ObjectOwnership.BUCKET_OWNER_ENFORCED)
      .build()

    new SampleS3Stack(stack, s3Bucket)
  }
}

class SampleS3Stack private (stack: MyStack, val s3Bucket: Bucket) extends StackWrapper(stack)
