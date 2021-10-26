package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
import software.amazon.awscdk.services.ec2.{Peer, Port, SecurityGroup, Vpc}

object SampleVpcStack {
  val id: StackId = StackId("vpc-stack")
}

class SampleVpcStack(args: StackArgs) extends AbstractStack(SampleVpcStack.id, args) {

  val (vpc, sgBastion, sgElb) = {

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

    (vpc, sgBastion, sgElb)
  }
}
