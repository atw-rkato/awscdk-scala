package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.TestOps.JsValueOps

class VpcTemplateSpec extends CdkSpecBase {

  test("testStack") {
    val template = createTemplate(VpcTemplateStack)

    val resources = template.get("Resources")
    val vpc = resources.get("SampleVpc07DAD426")
    val properties = vpc.get("Properties")
    vpc.get("Type").to[String] should ===("AWS::EC2::VPC")

    properties.get("CidrBlock").to[String] should ===("10.0.0.0/16")
  }
}
