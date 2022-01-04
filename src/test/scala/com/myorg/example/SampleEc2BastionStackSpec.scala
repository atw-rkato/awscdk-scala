package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.{TestJsValueOps, TestStackOps}
import com.myorg.lib.StackArgs
import play.api.libs.json.JsValue
import software.amazon.awscdk.core
import software.amazon.awscdk.core.AppProps

object SampleEc2BastionStackSpec {

  val InstanceId = "SampleEc2Bastion77096BC4"
  val RoleId     = "SampleEc2BastionInstanceRole9259FCCD"

  private def getTemplate(context: java.util.Map[String, Any] = TestProps.Context): JsValue = {
    val stackArgs       = StackArgs(new core.App(AppProps.builder().context(context).build()))
    val vpcStack        = new SampleVpcStack(stackArgs)
    val ec2BastionStack = new SampleEc2BastionStack(stackArgs, vpcStack.vpc, vpcStack.sgBastion)
    ec2BastionStack.toJson
  }
}

class SampleEc2BastionStackSpec extends CdkSpecBase {

  import SampleEc2BastionStackSpec._

  "EC2 for bastion" - {
    "env test" - {
      val template = getTemplate()

      "Instance" in {
        val instance = template.get("Resources").get(InstanceId)
        assert(instance.get("Type").to[String] === "AWS::EC2::Instance")

        val properties = instance.get("Properties")
        assert(properties.get("InstanceType").to[String] === "t2.micro")
        assert(properties.get("KeyName").to[String] === "testKeyName")
        assert(properties.get("UserData").get("Fn::Base64").to[String].nonEmpty)
      }
    }
  }
}
