package com.myorg.example

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
  UserData,
  Vpc,
}
import software.amazon.awscdk.services.iam.Role

object SampleEc2ServerStack extends StackFactory {
  val id: StackId = StackId("ec2-server-stack")

  def apply(stackArgs: StackArgs, vpc: Vpc, webRole: Role): SampleEc2ServerStack = {
    val stack      = MyStack(this.id, stackArgs)
    val keyName    = stack.tryGetContext[String]("keyName").get
    val userScript = io.Source.fromResource("user-data/user-data-for-server.sh").mkString

    val defaultSg = SecurityGroup.fromSecurityGroupId(stack, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val web01 = Instance.Builder
      .create(stack, "SampleEc2Web01")
      .instanceName("sample-ec2-web01")
      .machineImage(amazonLinux2Image(userScript))
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
      .role(webRole)
      .build()

    val web02 = Instance.Builder
      .create(stack, "SampleEc2Web02")
      .instanceName("sample-ec2-web02")
      .machineImage(amazonLinux2Image(userScript))
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
      .role(webRole)
      .build()

    new SampleEc2ServerStack(stack, web01, web02)
  }

  private def amazonLinux2Image(userScript: String): AmazonLinuxImage = {
    AmazonLinuxImage.Builder
      .create()
      .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
      .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
      .build()
  }
}

class SampleEc2ServerStack private (stack: MyStack, val web01: Instance, val web02: Instance)
    extends StackWrapper(stack)
