package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
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

object SampleEc2BastionStack {
  val id: StackId = StackId("ec2-bastion-stack")
}

class SampleEc2BastionStack(args: StackArgs, vpc: Vpc, sgBastion: SecurityGroup)
    extends AbstractStack(SampleEc2BastionStack.id, args) {

  val bastion: Instance = {
    val keyName    = tryGetContext[String]("keyName").get
    val userScript = io.Source.fromResource("user-data/user-data-for-bastion.sh").mkString

    val defaultSg = SecurityGroup.fromSecurityGroupId(this, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val bastion = Instance.Builder
      .create(this, "SampleEc2Bastion")
      .instanceName("sample-ec2-bastion")
      .machineImage(amazonLinux2Image(userScript))
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
      .securityGroup(defaultSg)
      .keyName(keyName)
      .build()

    bastion.addSecurityGroup(sgBastion)

    bastion
  }

  private def amazonLinux2Image(userScript: String) = {
    AmazonLinuxImage.Builder
      .create()
      .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
      .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
      .build()
  }
}
