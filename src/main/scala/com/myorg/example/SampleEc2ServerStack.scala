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
  UserData,
  Vpc,
}
import software.amazon.awscdk.services.iam.Role

object SampleEc2ServerStack {
  val id: StackId = StackId("ec2-server-stack")
}

class SampleEc2ServerStack(args: StackArgs, vpc: Vpc, webRole: Role)
    extends AbstractStack(SampleEc2ServerStack.id, args) {

  lazy val (web01: Instance, web02: Instance) = {
    val keyName    = tryGetContext[String]("keyName").get
    val userScript = io.Source.fromResource("user-data/user-data-for-server.sh").mkString

    val defaultSg = SecurityGroup.fromSecurityGroupId(this, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val web01 = Instance.Builder
      .create(this, "SampleEc2Web01")
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
      .create(this, "SampleEc2Web02")
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

    (web01, web02)
  }

  private def amazonLinux2Image(userScript: String) = {
    AmazonLinuxImage.Builder
      .create()
      .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
      .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
      .build()
  }
}
