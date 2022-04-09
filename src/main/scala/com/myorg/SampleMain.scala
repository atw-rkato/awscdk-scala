package com.myorg

import com.myorg.example.{
  SampleAlbStack,
  SampleEc2BastionStack,
  SampleEc2ServerStack,
  SampleIamStack,
  SampleRdsStack,
  SampleS3Stack,
  SampleVpcStack,
}
import com.myorg.lib.StackArgs
import software.amazon.awscdk.core

@main def main(): Unit = {
  val app = new core.App()

  createStacks(StackArgs(app))
  app.synth
}

private def createStacks(stackArgs: StackArgs): Unit = {
  val iamStack        = SampleIamStack(stackArgs)
  val s3Stack         = SampleS3Stack(stackArgs)
  val vpcStack        = SampleVpcStack(stackArgs)
  val vpc             = vpcStack.vpc
  val ec2BastionStack = SampleEc2BastionStack(stackArgs, vpc, vpcStack.sgBastion)
  val ec2ServerStack  = SampleEc2ServerStack(stackArgs, vpc, iamStack.webRole)
  val albStack        = SampleAlbStack(stackArgs, vpc, vpcStack.sgElb, ec2ServerStack.web01, ec2ServerStack.web02)
  val rdsStack        = SampleRdsStack(stackArgs, vpc)

  ()
}
