package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.services.ec2.{
  InstanceClass,
  InstanceSize,
  InstanceType,
  SecurityGroup,
  SubnetSelection,
  Vpc,
}
import software.amazon.awscdk.services.rds.{
  Credentials,
  DatabaseInstance,
  DatabaseInstanceEngine,
  MySqlInstanceEngineProps,
  MysqlEngineVersion,
  OptionGroup,
  ParameterGroup,
  SubnetGroup,
}

object SampleRdsStack {
  val id: StackId = StackId("rds-stack")
}

class SampleRdsStack(args: StackArgs, vpc: Vpc) extends AbstractStack(SampleRdsStack.id, args) {

  val db: DatabaseInstance = {
    val engine =
      DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder().version(MysqlEngineVersion.VER_8_0_26).build())

    val parameterGroup = ParameterGroup.Builder
      .create(this, "SampleDbPg")
      .engine(engine)
      .description("sample parameter group")
      .build()

    val optionGroup = OptionGroup.Builder
      .create(this, "SampleDbOg")
      .engine(engine)
      .description("sample option group")
      .configurations(jList())
      .build()

    val subnetGroup = SubnetGroup.Builder
      .create(this, "SampleDbSubnet")
      .subnetGroupName("sample-db-subnet")
      .description("sample db subnet")
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnets(vpc.getPrivateSubnets).build())
      .build()

    val defaultSg = SecurityGroup.fromSecurityGroupId(this, "DefaultSg", vpc.getVpcDefaultSecurityGroup)
    DatabaseInstance.Builder
      .create(this, "SampleDb")
      .instanceIdentifier("sample-db")
      .engine(engine)
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .allocatedStorage(20)
      .maxAllocatedStorage(1000)
      .backupRetention(Duration.days(7))
      .vpc(vpc)
      .subnetGroup(subnetGroup)
      .publiclyAccessible(false)
      .securityGroups(jList(defaultSg))
      .credentials(Credentials.fromUsername("admin"))
      .parameterGroup(parameterGroup)
      .optionGroup(optionGroup)
      .build()
  }
}
