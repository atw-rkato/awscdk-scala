package com.myorg

import com.myorg.example.{Ec2BastionStack, VpcStack}
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
    val vpcStack   = VpcStack(stackArgs)
    val vpc        = vpcStack.vpc
    val ec2Bastion = Ec2BastionStack(stackArgs, vpc, vpcStack.sgBastion)

    ()
  }
}
