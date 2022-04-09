package com.myorg.example

import com.myorg.lib.{MyStack, StackContext, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.services.ec2.{Peer, Port, SecurityGroup, Vpc}

object SampleVpcStack extends StackFactory {
  val id: StackId = StackId("vpc-stack")

  def apply()(implicit ctx: StackContext): SampleVpcStack = {
    implicit val stack: MyStack = MyStack(this.id)

    val vpc = Vpc.Builder
      .create(stack, "SampleVpc")
      .cidr("10.0.0.0/16")
      .maxAzs(2)
      .build()

    val sgBastion = SecurityGroup.Builder
      .create(stack, "SampleSgBastion")
      .securityGroupName("sample-sg-bastion")
      .description("for bastion server")
      .vpc(vpc)
      .build()
    sgBastion.addIngressRule(Peer.anyIpv4(), Port.tcp(22))

    val sgElb = SecurityGroup.Builder
      .create(stack, "SampleSgElb")
      .securityGroupName("sample-sg-elb")
      .description("for load balancer")
      .vpc(vpc)
      .build()
    sgElb.addIngressRule(Peer.anyIpv4(), Port.tcp(80))
    sgElb.addIngressRule(Peer.anyIpv4(), Port.tcp(443))

    SampleVpcStack(vpc, sgBastion, sgElb)
  }
}

case class SampleVpcStack private (vpc: Vpc, sgBastion: SecurityGroup, sgElb: SecurityGroup)(implicit stack: MyStack)
    extends StackWrapper(stack)
