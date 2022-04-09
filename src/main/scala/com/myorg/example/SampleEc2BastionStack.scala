package com.myorg.example

import com.myorg.example.context.ContextKeys
import com.myorg.lib.{MyStack, StackArgs, StackFactory, StackId, StackWrapper}
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

object SampleEc2BastionStack extends StackFactory {
  val id: StackId = StackId("ec2-bastion-stack")

  def apply(stackArgs: StackArgs, vpc: Vpc, sgBastion: SecurityGroup): SampleEc2BastionStack = {
    val stack      = MyStack(this.id, stackArgs)
    val keyName    = stack.tryGetContext[String](ContextKeys.KeyName).get
    val userScript = io.Source.fromResource("user-data/user-data-for-bastion.sh").mkString

    val defaultSg = SecurityGroup.fromSecurityGroupId(stack, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val bastion = Instance.Builder
      .create(stack, "SampleEc2Bastion")
      .instanceName("sample-ec2-bastion")
      .machineImage(amazonLinux2Image(userScript))
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
      .securityGroup(defaultSg)
      .keyName(keyName)
      .build()

    bastion.addSecurityGroup(sgBastion)

    new SampleEc2BastionStack(stack, bastion)
  }

  private def amazonLinux2Image(userScript: String) = {
    AmazonLinuxImage.Builder
      .create()
      .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
      .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
      .build()
  }
}

class SampleEc2BastionStack private (stack: MyStack, val bastion: Instance) extends StackWrapper(stack)
