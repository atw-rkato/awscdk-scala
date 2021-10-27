package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
import software.amazon.awscdk.services.ec2.{Instance, SecurityGroup, SubnetSelection, Vpc}
import software.amazon.awscdk.services.elasticloadbalancingv2.targets.InstanceTarget
import software.amazon.awscdk.services.elasticloadbalancingv2.{
  ApplicationLoadBalancer,
  ApplicationProtocol,
  ApplicationTargetGroup,
  BaseApplicationListenerProps,
}

object SampleAlbStack {
  val id: StackId = StackId("alb-stack")
}

class SampleAlbStack(args: StackArgs, vpc: Vpc, sgElb: SecurityGroup, web01: Instance, web02: Instance)
    extends AbstractStack(SampleAlbStack.id, args) {

  val alb: ApplicationLoadBalancer = {
    val targetGroup = ApplicationTargetGroup.Builder
      .create(this, "sample-tg")
      .vpc(vpc)
      .protocol(ApplicationProtocol.HTTP)
      .targets(
        jList(
          new InstanceTarget(web01, 3000),
          new InstanceTarget(web02, 3000),
        )
      )
      .build()

    val defaultSg = SecurityGroup.fromSecurityGroupId(this, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val alb = ApplicationLoadBalancer.Builder
      .create(this, "SampleElb")
      .loadBalancerName("sample-elb")
      .internetFacing(true)
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnets(vpc.getPublicSubnets).build())
      .securityGroup(defaultSg)
      .build()

    alb.addSecurityGroup(sgElb)
    alb.addListener(
      "HTTP:80",
      BaseApplicationListenerProps
        .builder()
        .protocol(ApplicationProtocol.HTTP)
        .defaultTargetGroups(jList(targetGroup))
        .build(),
    )

    alb
  }
}
