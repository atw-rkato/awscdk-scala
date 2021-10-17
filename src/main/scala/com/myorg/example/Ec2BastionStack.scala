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
  SubnetType,
  UserData,
  Vpc,
}

class Ec2BastionStack private (stack: CustomStack, val bastion: Instance) extends StackWrapper(stack)

object Ec2BastionStack extends StackFactory {
  val id: StackId = StackId("ec2-bastion-stack")

  def apply(args: StackArgs, vpc: Vpc, sgBastion: SecurityGroup): Ec2BastionStack = {
    val stack      = new CustomStack(id, args)
    val keyName    = stack.tryGetContext[String]("keyName").get
    val userScript = io.Source.fromResource("user-data/user-data-for-bastion.sh").mkString

    val defaultSg = SecurityGroup.fromSecurityGroupId(stack, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val bastion = Instance.Builder
      .create(stack, "sample-ec2-bastion")
      .machineImage(
        AmazonLinuxImage.Builder
          .create()
          .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
          .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
          .build()
      )
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
      .securityGroup(defaultSg)
      .keyName(keyName)
      .build()

    bastion.addSecurityGroup(sgBastion)

    new Ec2BastionStack(stack, bastion)
  }
}
