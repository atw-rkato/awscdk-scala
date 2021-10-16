package com.myorg.example

import com.myorg.lib.{CustomStack, StackArgs, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.services.ec2.{ISecurityGroup, IVpc, Peer, Port, SecurityGroup, Vpc}

class VpcStack private (stack: CustomStack, val vpc: IVpc, val sgBastion: ISecurityGroup, val sgElb: ISecurityGroup)
    extends StackWrapper(stack)

object VpcStack extends StackFactory {
  val id: StackId = StackId("vpc-stack")

  def apply(args: StackArgs): VpcStack = {
    val stack = new CustomStack(id, args)

    val vpc: IVpc = Vpc.Builder
      .create(stack, "SampleVpc")
      .cidr("10.0.0.0/16")
      .maxAzs(2)
      .build()

    val sgBastion: ISecurityGroup = {
      val sg = SecurityGroup.Builder
        .create(stack, "SampleSgBastion")
        .securityGroupName("sample-sg-bastion")
        .description("for bastion server")
        .vpc(vpc)
        .build()

      sg.addIngressRule(Peer.anyIpv4, Port.tcp(22))
      sg
    }

    val sgElb: ISecurityGroup = {
      val sg = SecurityGroup.Builder
        .create(stack, "SampleSgElb")
        .securityGroupName("sample-sg-elb")
        .description("for load balancer")
        .vpc(vpc)
        .build()

      sg.addIngressRule(Peer.anyIpv4, Port.tcp(80))
      sg.addIngressRule(Peer.anyIpv4, Port.tcp(443))
      sg
    }

    new VpcStack(stack, vpc, sgBastion, sgElb)
  }
}
