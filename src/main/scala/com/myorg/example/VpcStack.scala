package com.myorg.example

import com.myorg.lib.{StackArgs, StackBase, StackId}
import software.amazon.awscdk.services.ec2._

object VpcStack {
  val id: StackId = StackId("vpc-stack")
}

class VpcStack(args: StackArgs) extends StackBase(VpcStack.id, args) {

  val vpc: IVpc = Vpc.Builder
    .create(this, "SampleVpc")
    .cidr("10.0.0.0/16")
    .maxAzs(2)
    .build()

  val sgBastion: ISecurityGroup = {
    val sg = SecurityGroup.Builder
      .create(this, "SampleSgBastion")
      .securityGroupName("sample-sg-bastion")
      .description("for bastion server")
      .vpc(vpc)
      .build()

    sg.addIngressRule(Peer.anyIpv4, Port.tcp(22))
    sg
  }

  val sgElb: ISecurityGroup = {
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
