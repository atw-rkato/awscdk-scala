package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.services.s3.{BlockPublicAccess, Bucket, ObjectOwnership}

object SampleS3Stack {
  val id: StackId = StackId("s3-stack")
}

class SampleS3Stack(args: StackArgs) extends AbstractStack(SampleS3Stack.id, args) {

  val s3Bucket: Bucket = Bucket.Builder
    .create(this, "SampleS3Bucket")
    .bucketName("my-sample-s3-bucket-ap-northeast-1")
    .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
    .removalPolicy(RemovalPolicy.DESTROY)
    .objectOwnership(ObjectOwnership.BUCKET_OWNER_ENFORCED)
    .build()
}
