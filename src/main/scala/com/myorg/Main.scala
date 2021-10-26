package com.myorg

import com.myorg.example.{SampleAlbStack, SampleEc2BastionStack, SampleEc2ServerStack, SampleVpcStack}
import com.myorg.lib.StackArgs
import software.amazon.awscdk.core

object Main {

  def main(args: Array[String]): Unit = {
    val app = new core.App

    createStacks(StackArgs(app))

    app.synth

    ()
  }

  private def createStacks(stackArgs: StackArgs): Unit = {
    val vpcStack        = new SampleVpcStack(stackArgs)
    val vpc             = vpcStack.vpc
    val ec2BastionStack = new SampleEc2BastionStack(stackArgs, vpc, vpcStack.sgBastion)
    val ec2ServerStack  = new SampleEc2ServerStack(stackArgs, vpc)
    val albStack        = new SampleAlbStack(stackArgs, vpc, vpcStack.sgElb, ec2ServerStack.web01, ec2ServerStack.web02)

    ()
  }
}
