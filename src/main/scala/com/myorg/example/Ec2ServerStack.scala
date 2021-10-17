package com.myorg.example

import com.myorg.lib.{CustomStack, StackArgs, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.services.ec2.{
  AmazonLinuxGeneration,
  AmazonLinuxImage,
  Instance,
  InstanceClass,
  InstanceSize,
  InstanceType,
  LinuxUserDataOptions,
  SecurityGroup,
  SubnetSelection,
  UserData,
  Vpc,
}

class Ec2ServerStack private (stack: CustomStack, val web01: Instance, val web02: Instance) extends StackWrapper(stack)

object Ec2ServerStack extends StackFactory {
  val id: StackId = StackId("ec2-server-stack")

  def apply(args: StackArgs, vpc: Vpc): Ec2ServerStack = {
    val stack      = new CustomStack(id, args)
    val keyName    = stack.tryGetContext[String]("keyName").get
    val userScript = io.Source.fromResource("user-data/user-data-for-server.sh").mkString

    val defaultSg = SecurityGroup.fromSecurityGroupId(stack, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val web01 = Instance.Builder
      .create(stack, "sample-ec2-web01")
      .machineImage(
        AmazonLinuxImage.Builder
          .create()
          .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
          .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
          .build()
      )
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .vpc(vpc)
      .vpcSubnets(
        SubnetSelection
          .builder()
          .subnets(jList(vpc.getPrivateSubnets.get(0)))
          .build()
      )
      .securityGroup(defaultSg)
      .keyName(keyName)
      .build()

    val web02 = Instance.Builder
      .create(stack, "sample-ec2-web02")
      .machineImage(
        AmazonLinuxImage.Builder
          .create()
          .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
          .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
          .build()
      )
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .vpc(vpc)
      .vpcSubnets(
        SubnetSelection
          .builder()
          .subnets(jList(vpc.getPrivateSubnets.get(1)))
          .build()
      )
      .securityGroup(defaultSg)
      .keyName(keyName)
      .build()

    new Ec2ServerStack(stack, web01, web02)
  }
}
