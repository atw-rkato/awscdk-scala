package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.{JsValueOps, StackOps}

class VpcTemplateSpec extends CdkSpecBase {

  test("testStack") {
    val template = new VpcStack(testArgs).toJson
    val vpcId    = "SampleVpc07DAD426"

    val resources = template.get("Resources")

    val vpc        = resources.get(vpcId)
    val properties = vpc.get("Properties")
    vpc.get("Type").to[String] should ===("AWS::EC2::VPC")

    properties.get("CidrBlock").to[String] should ===("10.0.0.0/16")
    properties.get("EnableDnsHostnames").to[Boolean] should ===(true)
    properties.get("EnableDnsSupport").to[Boolean] should ===(true)
    properties.get("InstanceTenancy").to[String] should ===("default")
  }
}
