package com.myorg.example

import com.myorg.lib.{MyStack, StackContext, StackFactory, StackId, StackWrapper}
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

  def apply(vpc: Vpc, webRole: Role)(implicit ctx: StackContext): SampleEc2ServerStack = {
    implicit val stack: MyStack = MyStack(this.id)

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

    SampleEc2ServerStack(web01, web02)
  }

  private def amazonLinux2Image(userScript: String): AmazonLinuxImage = {
    AmazonLinuxImage.Builder
      .create()
      .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
      .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
      .build()
  }
}

case class SampleEc2ServerStack private (web01: Instance, web02: Instance)(implicit stack: MyStack)
    extends StackWrapper(stack)
