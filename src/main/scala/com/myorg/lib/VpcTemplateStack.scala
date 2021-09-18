package com.myorg.lib

import software.amazon.awscdk.core.{Construct, Stack, StackProps}
import software.amazon.awscdk.services.ec2.CfnVPC

class VpcTemplateStack {

  def createResources(scope: Construct, id: String, props: StackProps): Stack = createResources(scope, id, Some(props))

  def createResources(scope: Construct, id: String, props: Option[StackProps] = None): Stack = {
    val stack = new Stack(scope, id, props.orNull)

    CfnVPC.Builder.create(stack, "MyVpc").cidrBlock("10.0.0.0/16").build()

    stack
  }
}
