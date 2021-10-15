package com.myorg

import com.myorg.example.{Ec2BastionStack, VpcStack}
import com.myorg.lib.StackArgs
import software.amazon.awscdk

object Main {

  def main(args: Array[String]): Unit = {
    val app = new awscdk.core.App

    val stackArgs  = StackArgs(app)
    val vpcStack   = new VpcStack(stackArgs)
    val vpc        = vpcStack.vpc
    val ec2Bastion = new Ec2BastionStack(stackArgs, vpc, vpcStack.sgBastion)

    app.synth

    ()
  }
}
