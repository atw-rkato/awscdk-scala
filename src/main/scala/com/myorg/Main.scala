package com.myorg

import com.myorg.example.{AlbStack, Ec2BastionStack, Ec2ServerStack, VpcStack}
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
    val vpcStack        = VpcStack(stackArgs)
    val vpc             = vpcStack.vpc
    val ec2BastionStack = Ec2BastionStack(stackArgs, vpc, vpcStack.sgBastion)
    val ec2ServerStack  = Ec2ServerStack(stackArgs, vpc)
    val albStack        = AlbStack(stackArgs, vpc, vpcStack.sgElb, ec2ServerStack.web01, ec2ServerStack.web02)

    ()
  }
}
