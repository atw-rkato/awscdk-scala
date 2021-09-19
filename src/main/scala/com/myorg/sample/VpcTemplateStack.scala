package com.myorg.sample

import software.amazon.awscdk.core.{Construct, StackProps}
import software.amazon.awscdk.services.ec2.CfnVPC

class VpcTemplateStack(scope: Construct, id: String, props: Option[StackProps] = None)
    extends StackBase(scope, id, props) {

  def this(scope: Construct, id: String, props: StackProps) = this(scope, id, Some(props))

  CfnVPC.Builder.create(this, "MyVpc").cidrBlock("10.0.0.0/16").build()
}
