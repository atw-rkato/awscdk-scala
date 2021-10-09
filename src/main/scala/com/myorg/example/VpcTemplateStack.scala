package com.myorg.example

import com.myorg.lib.{StackBase, StackFactory, StackId}
import software.amazon.awscdk.core.{Construct, StackProps}
import software.amazon.awscdk.services.ec2._

object VpcTemplateStack extends StackFactory[VpcTemplateStack] {
  val id: StackId = StackId("vpc-template")

  def apply(scope: Construct, props: Option[StackProps]) = new VpcTemplateStack(scope, id, props)
}

class VpcTemplateStack private (scope: Construct, id: StackId, props: Option[StackProps])
    extends StackBase(scope, id, props) {

  {
    val vpc = Vpc.Builder
      .create(this, "SampleVpc")
      .cidr("10.0.0.0/16")
      .maxAzs(2)
      .build()

    val sgBastion = {
      val sg = SecurityGroup.Builder
        .create(this, "SampleSgBastion")
        .securityGroupName("sample-sg-bastion")
        .description("for bastion server")
        .vpc(vpc)
        .build()

      sg.addIngressRule(Peer.anyIpv4, Port.tcp(22))
      sg
    }

    val sgElb = {
      val sg = SecurityGroup.Builder
        .create(this, "SampleSgElb")
        .securityGroupName("sample-sg-elb")
        .description("for load balancer")
        .vpc(vpc)
        .build()

      sg.addIngressRule(Peer.anyIpv4, Port.tcp(80))
      sg.addIngressRule(Peer.anyIpv4, Port.tcp(443))
      sg
    }
  }
}
