package com.myorg.example

import com.myorg.lib.{CustomStack, CustomStackWrapper, StackArgs, StackFactory, StackId}
import software.amazon.awscdk.services.ec2.{Peer, Port, SecurityGroup, Vpc}

class SampleVpcStack private (
  stack: CustomStack,
  val vpc: Vpc,
  val sgBastion: SecurityGroup,
  val sgElb: SecurityGroup,
) extends CustomStackWrapper(stack)

object SampleVpcStack extends StackFactory {
  val id: StackId = StackId("vpc-stack")

  def apply(args: StackArgs): SampleVpcStack = {
    val stack = new CustomStack(id, args)

    val vpc = Vpc.Builder
      .create(stack, "SampleVpc")
      .cidr("10.0.0.0/16")
      .maxAzs(2)
      .build()

    val sgBastion = {
      val sg = SecurityGroup.Builder
        .create(stack, "SampleSgBastion")
        .securityGroupName("sample-sg-bastion")
        .description("for bastion server")
        .vpc(vpc)
        .build()

      sg.addIngressRule(Peer.anyIpv4, Port.tcp(22))
      sg
    }

    val sgElb = {
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

    new SampleVpcStack(stack, vpc, sgBastion, sgElb)
  }
}
