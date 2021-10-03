package com.myorg.example

import com.myorg.lib.{StackBase, StackFactory, StackId}
import software.amazon.awscdk.core.{Construct, StackProps, Tags}
import software.amazon.awscdk.services.ec2.{SubnetConfiguration, SubnetType, Vpc}

object VpcTemplateStack extends StackFactory[VpcTemplateStack] {
  val id: StackId                                        = StackId("vpc-template")
  def apply(scope: Construct, props: Option[StackProps]) = new VpcTemplateStack(scope, id, props)
}

class VpcTemplateStack private (scope: Construct, id: StackId, props: Option[StackProps])
    extends StackBase(scope, id, props) {

  {
    @annotation.nowarn
    val vpc = Vpc.Builder
      .create(this, "SampleVpc")
      .cidr("10.0.0.0/16")
      .maxAzs(2)
      .subnetConfiguration(
        jList(
          SubnetConfiguration
            .builder()
            .name("sample-subnet-public01")
            .cidrMask(20)
            .subnetType(SubnetType.PUBLIC)
            .build(),
          SubnetConfiguration
            .builder()
            .name("sample-subnet-public02")
            .cidrMask(20)
            .subnetType(SubnetType.PUBLIC)
            .build(),
          SubnetConfiguration
            .builder()
            .name("sample-subnet-private01")
            .cidrMask(20)
            .subnetType(SubnetType.PRIVATE)
            .build(),
          SubnetConfiguration
            .builder()
            .name("sample-subnet-private02")
            .cidrMask(20)
            .subnetType(SubnetType.PRIVATE)
            .build(),
        )
      )
      .build()

    Tags.of(vpc).add("Name", "sample-vpc")
  }
}
