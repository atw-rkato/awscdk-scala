package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.{TestJsValueOps, TestStackOps}
import com.myorg.lib.StackArgs
import play.api.libs.json.{JsArray, JsValue, Json}
import software.amazon.awscdk.core
import software.amazon.awscdk.core.AppProps

class SampleVpcStackSpec extends CdkSpecBase {

  val vpcId       = "SampleVpc07DAD426"
  val sgBastionId = "SampleSgBastion1679A31B"
  val sgElbId     = "SampleSgElb3916B66D"

  private def getTemplate(context: java.util.Map[String, Any] = TestProps.Context): JsValue = {
    val stackArgs = StackArgs(new core.App(AppProps.builder().context(context).build()))
    val vpcStack  = new SampleVpcStack(stackArgs)
    vpcStack.toJson
  }

  "VPC" - {
    "env test" in {
      val template = getTemplate()

      val vpc = template.get("Resources").get(vpcId)
      assert(vpc.get("Type").to[String] === "AWS::EC2::VPC")

      val properties = vpc.get("Properties")
      assert(properties.get("CidrBlock").to[String] === "10.0.0.0/16")
      assert(properties.get("EnableDnsHostnames").to[Boolean] === true)
      assert(properties.get("EnableDnsSupport").to[Boolean] === true)
      assert(properties.get("InstanceTenancy").to[String] === "default")
    }
  }

  "SecurityGroup for bastion server" - {
    "env test" in {
      val template = getTemplate()

      val sgBastion = template.get("Resources").get(sgBastionId)
      assert(sgBastion.get("Type").to[String] === "AWS::EC2::SecurityGroup")

      val properties = sgBastion.get("Properties")
      assert(properties.get("GroupName").to[String] === "sample-sg-bastion")

      val egress = properties.get("SecurityGroupEgress").to[JsArray]
      assert(egress.value.size === 1)
      assert(egress(0).get("CidrIp").to[String] === "0.0.0.0/0")
      assert(egress(0).get("IpProtocol").to[String] === "-1")

      val ingress = properties.get("SecurityGroupIngress").to[JsArray]
      assert(ingress.value.size === 1)
      assert(ingress(0).get("CidrIp").to[String] === "0.0.0.0/0")
      assert(ingress(0).get("IpProtocol").to[String] === "tcp")
      assert(ingress(0).get("FromPort").to[Int] === 22)
      assert(ingress(0).get("ToPort").to[Int] === 22)

      assert(properties.get("VpcId") === Json.obj("Ref" -> vpcId))
    }
  }

  "SecurityGroup for load balancer" - {
    "env test" in {
      val template = getTemplate()

      val sgElb = template.get("Resources").get(sgElbId)
      assert(sgElb.get("Type").to[String] === "AWS::EC2::SecurityGroup")

      val properties = sgElb.get("Properties")
      assert(properties.get("GroupName").to[String] === "sample-sg-elb")

      val egress = properties.get("SecurityGroupEgress").to[JsArray]
      assert(egress.value.size === 1)
      assert(egress(0).get("CidrIp").to[String] === "0.0.0.0/0")
      assert(egress(0).get("IpProtocol").to[String] === "-1")

      val ingress = properties.get("SecurityGroupIngress").to[JsArray]
      assert(ingress.value.size === 2)
      assert(ingress(0).get("CidrIp").to[String] === "0.0.0.0/0")
      assert(ingress(0).get("IpProtocol").to[String] === "tcp")
      assert(ingress(0).get("FromPort").to[Int] === 80)
      assert(ingress(0).get("ToPort").to[Int] === 80)
      assert(ingress(1).get("CidrIp").to[String] === "0.0.0.0/0")
      assert(ingress(1).get("IpProtocol").to[String] === "tcp")
      assert(ingress(1).get("FromPort").to[Int] === 443)
      assert(ingress(1).get("ToPort").to[Int] === 443)

      assert(properties.get("VpcId") === Json.obj("Ref" -> vpcId))
    }
  }
}
