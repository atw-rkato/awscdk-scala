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

object Main {

  def main(args: Array[String]): Unit = {
    val app = new core.App

    createStacks(StackArgs(app))

    app.synth

    ()
  }

  //noinspection ScalaUnusedSymbol
  private def createStacks(stackArgs: StackArgs): Unit = {
    val iamStack        = new SampleIamStack(stackArgs)
    val webRole         = iamStack.webRole
    val s3Stack         = new SampleS3Stack(stackArgs)
    val vpcStack        = new SampleVpcStack(stackArgs)
    val vpc             = vpcStack.vpc
    val ec2BastionStack = new SampleEc2BastionStack(stackArgs, vpc, vpcStack.sgBastion)
    val ec2ServerStack  = new SampleEc2ServerStack(stackArgs, vpc, webRole)
    val albStack        = new SampleAlbStack(stackArgs, vpc, vpcStack.sgElb, ec2ServerStack.web01, ec2ServerStack.web02)
    val rdsStack        = new SampleRdsStack(stackArgs, vpc)

    ()
  }
}
