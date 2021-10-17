package com.myorg.example

import com.myorg.lib.{CustomStack, CustomStackWrapper, StackArgs, StackFactory, StackId}
import software.amazon.awscdk.services.ec2.{Instance, SecurityGroup, SubnetSelection, Vpc}
import software.amazon.awscdk.services.elasticloadbalancingv2.targets.InstanceTarget
import software.amazon.awscdk.services.elasticloadbalancingv2.{
  ApplicationLoadBalancer,
  ApplicationProtocol,
  ApplicationTargetGroup,
  BaseApplicationListenerProps,
}

class SampleAlbStack private (stack: CustomStack, val alb: ApplicationLoadBalancer) extends CustomStackWrapper(stack)

object SampleAlbStack extends StackFactory {
  val id: StackId = StackId("alb-stack")

  def apply(args: StackArgs, vpc: Vpc, sgElb: SecurityGroup, web01: Instance, web02: Instance): SampleAlbStack = {
    val stack = new CustomStack(id, args)

    val defaultSg = SecurityGroup.fromSecurityGroupId(stack, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val alb = ApplicationLoadBalancer.Builder
      .create(stack, "SampleElb")
      .loadBalancerName("sample-elb")
      .internetFacing(true)
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnets(vpc.getPublicSubnets).build())
      .securityGroup(defaultSg)
      .build()

    alb.addSecurityGroup(sgElb)

    val targetGroup = ApplicationTargetGroup.Builder
      .create(stack, "sample-tg")
      .vpc(vpc)
      .protocol(ApplicationProtocol.HTTP)
      .targets(
        jList(
          new InstanceTarget(web01, 3000),
          new InstanceTarget(web02, 3000),
        )
      )
      .build()

    alb.addListener(
      "HTTP:80",
      BaseApplicationListenerProps
        .builder()
        .protocol(ApplicationProtocol.HTTP)
        .defaultTargetGroups(jList(targetGroup))
        .build(),
    )

    new SampleAlbStack(stack, alb)
  }
}
