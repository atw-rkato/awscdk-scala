package com.myorg.example

import com.myorg.lib.{MyStack, StackContext, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.core.{Duration, RemovalPolicy}
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

object SampleRdsStack extends StackFactory {
  val id: StackId = StackId("rds-stack")

  def apply(vpc: Vpc)(implicit ctx: StackContext): SampleRdsStack = {
    implicit val stack: MyStack = MyStack(this.id)

    val engine =
      DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder().version(MysqlEngineVersion.VER_8_0_26).build())

    val parameterGroup = ParameterGroup.Builder
      .create(stack, "SampleDbPg")
      .engine(engine)
      .description("sample parameter group")
      .build()

    val optionGroup = OptionGroup.Builder
      .create(stack, "SampleDbOg")
      .engine(engine)
      .description("sample option group")
      .configurations(jList())
      .build()

    val subnetGroup = SubnetGroup.Builder
      .create(stack, "SampleDbSubnet")
      .subnetGroupName("sample-db-subnet")
      .description("sample db subnet")
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnets(vpc.getPrivateSubnets).build())
      .build()

    val defaultSg = SecurityGroup.fromSecurityGroupId(stack, "DefaultSg", vpc.getVpcDefaultSecurityGroup)

    val db = DatabaseInstance.Builder
      .create(stack, "SampleDb")
      .instanceIdentifier("sample-db")
      .engine(engine)
      .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
      .removalPolicy(RemovalPolicy.DESTROY)
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

    SampleRdsStack(db)
  }
}

case class SampleRdsStack private (db: DatabaseInstance)(implicit stack: MyStack) extends StackWrapper(stack)
