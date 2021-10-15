package com.myorg.example

import com.myorg.lib.{StackArgs, StackBase, StackId}
import software.amazon.awscdk.services.ec2._

object Ec2BastionStack {
  val id: StackId = StackId("ec2-bastion-stack")
}

class Ec2BastionStack(args: StackArgs, vpc: IVpc, sgBastion: ISecurityGroup)
    extends StackBase(Ec2BastionStack.id, args) {

  val bastion: IInstance = {
    val keyName = tryGetContext[String]("keyName").get

    val userScript: String = io.Source.fromResource("user-data/user-data-for-bastion.sh").mkString

    val instance = Instance.Builder
      .create(this, "sample-ec2-bastion")
      .machineImage(
        MachineImage.latestAmazonLinux(
          AmazonLinuxImageProps
            .builder()
            .cpuType(AmazonLinuxCpuType.X86_64)
            .edition(AmazonLinuxEdition.STANDARD)
            .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
            .storage(AmazonLinuxStorage.GENERAL_PURPOSE)
            .userData(UserData.forLinux(LinuxUserDataOptions.builder().shebang(userScript).build()))
            .virtualization(AmazonLinuxVirt.HVM)
            .build()
        )
      )
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
      .securityGroup(sgBastion)
      .keyName(keyName)
      .build()

    instance
  }
}
