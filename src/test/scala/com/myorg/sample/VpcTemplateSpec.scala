package com.myorg.sample

import com.myorg.CdkSpecBase
import com.myorg.TestOps.JsValueOps
import software.amazon.awscdk

class VpcTemplateSpec extends CdkSpecBase {

  test("testStack") {
    val app      = new awscdk.core.App
    val stack    = new VpcTemplateStack(app, "Test")
    val template = getTemplate(app, stack)

    val resources  = template.get("Resources")
    val vpc        = resources.get("MyVpc")
    val properties = vpc.get("Properties")
    vpc.get("Type").to[String] should ===("AWS::EC2::VPC")

    properties.get("CidrBlock").to[String] should ===("10.0.0.0/16")
  }
}
