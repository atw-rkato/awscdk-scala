package com.myorg.lib

import com.myorg.CdkSpecBase
import com.myorg.TestOps.JsValueOps
import software.amazon.awscdk

class VpcTemplateSpec extends CdkSpecBase {

  test("testStack") {
    val app      = new awscdk.core.App
    val stack    = new VpcTemplateStack().createResources(app, "Test")
    val template = createTemplate(app, stack)

    val resources  = template.get("Resources")
    val vpc        = resources.get("MyVpc")
    val properties = vpc.get("Properties")
    vpc.getAs[String]("Type") should ===("AWS::EC2::VPC")

    properties.getAs[String]("CidrBlock") should ===("10.0.0.0/16")
  }
}
