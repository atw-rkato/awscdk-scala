package com.myorg.example

import com.myorg.lib.{CustomStack, StackArgs, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.services.ec2.{
  AmazonLinuxCpuType,
  AmazonLinuxEdition,
  AmazonLinuxGeneration,
  AmazonLinuxImageProps,
  AmazonLinuxStorage,
  AmazonLinuxVirt,
  IInstance,
  ISecurityGroup,
  IVpc,
  Instance,
  InstanceClass,
  InstanceSize,
  InstanceType,
  LinuxUserDataOptions,
  MachineImage,
  SubnetSelection,
  SubnetType,
  UserData,
}

class Ec2BastionStack private (stack: CustomStack, val bastion: IInstance) extends StackWrapper(stack)

object Ec2BastionStack extends StackFactory {
  val id: StackId = StackId("ec2-bastion-stack")

  def apply(args: StackArgs, vpc: IVpc, sgBastion: ISecurityGroup): Ec2BastionStack = {
    val stack   = new CustomStack(id, args)
    val keyName = stack.tryGetContext[String]("keyName").get
    val bastion = {
      val userScript: String = io.Source.fromResource("user-data/user-data-for-bastion.sh").mkString
      Instance.Builder
        .create(stack, "sample-ec2-bastion")
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
    }

    new Ec2BastionStack(stack, bastion)
  }
}
