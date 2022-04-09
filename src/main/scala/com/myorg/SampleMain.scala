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
import com.myorg.lib.StackContext
import software.amazon.awscdk.core
import software.amazon.awscdk.core.StackProps

@main def main(): Unit = {
  val app = new core.App()

  createStacks()(StackContext(app))
  app.synth
}

private def createStacks()(implicit ctx: StackContext): Unit = {
  val SampleIamStack(webRole)               = SampleIamStack()
  val _                                     = SampleS3Stack()
  val SampleVpcStack(vpc, sgBastion, sgElb) = SampleVpcStack()
  val _                                     = SampleEc2BastionStack(vpc, sgBastion)
  val SampleEc2ServerStack(web01, web02)    = SampleEc2ServerStack(vpc, webRole)
  val _                                     = SampleAlbStack(vpc, sgElb, web01, web02)
  val _                                     = SampleRdsStack(vpc)

  ()
}
