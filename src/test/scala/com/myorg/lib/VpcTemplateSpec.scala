package com.myorg.lib

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.createTemplate
import com.myorg.TestOps.{JsLookupResultOps, JsReadableOps}
import software.amazon.awscdk

class VpcTemplateSpec extends CdkSpecBase {

  test("testStack") {
    val app      = new awscdk.core.App
    val stack    = new VpcTemplateStack().createResources(app, "Test")
    val template = createTemplate(app, stack)

    val resources  = (template \ "Resources").orFail
    val vpc        = (resources \ "MyVpc").orFail
    val properties = (vpc \ "Properties").orFail
    (vpc \ "Type").decode[String] should ===("AWS::EC2::VPC")

    (properties \ "CidrBlock").decode[String] should ===("10.0.0.0/16")
  }
}
