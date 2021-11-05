package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.{TestJsValueOps, TestStackOps}
import com.myorg.lib.StackArgs
import play.api.libs.json.{JsArray, Json}
import software.amazon.awscdk.core

class VpcTemplateSpec extends CdkSpecBase {

  val vpcId       = "SampleVpc07DAD426"
  val sgBastionId = "SampleSgBastion1679A31B"
  val sgElbId     = "SampleSgElb3916B66D"

  test("test VPC") {
    val vpcStack = new SampleVpcStack(StackArgs(new core.App))
    val template = vpcStack.toJson

    val vpc = template.get("Resources").get(vpcId)
    vpc.get("Type").to[String] must ===("AWS::EC2::VPC")

    val properties = vpc.get("Properties")
    properties.get("CidrBlock").to[String] must ===("10.0.0.0/16")
    properties.get("EnableDnsHostnames").to[Boolean] must ===(true)
    properties.get("EnableDnsSupport").to[Boolean] must ===(true)
    properties.get("InstanceTenancy").to[String] must ===("default")
  }

  test("test SecurityGroup for bastion server") {
    val vpcStack = new SampleVpcStack(StackArgs(new core.App))
    val template = vpcStack.toJson

    val sgBastion = template.get("Resources").get(sgBastionId)
    sgBastion.get("Type").to[String] must ===("AWS::EC2::SecurityGroup")

    val properties = sgBastion.get("Properties")
    properties.get("GroupName").to[String] must ===("sample-sg-bastion")

    val egress = properties.get("SecurityGroupEgress").to[JsArray]
    egress.value.size must ===(1)
    egress(0).get("CidrIp").to[String] must ===("0.0.0.0/0")
    egress(0).get("IpProtocol").to[String] must ===("-1")

    val ingress = properties.get("SecurityGroupIngress").to[JsArray]
    ingress.value.size must ===(1)
    ingress(0).get("CidrIp").to[String] must ===("0.0.0.0/0")
    ingress(0).get("IpProtocol").to[String] must ===("tcp")
    ingress(0).get("FromPort").to[Int] must ===(22)
    ingress(0).get("ToPort").to[Int] must ===(22)

    properties.get("VpcId") must ===(Json.obj("Ref" -> vpcId))
  }

  test("test SecurityGroup for  load balancer") {
    val vpcStack = new SampleVpcStack(StackArgs(new core.App))
    val template = vpcStack.toJson

    val sgElb = template.get("Resources").get(sgElbId)
    sgElb.get("Type").to[String] must ===("AWS::EC2::SecurityGroup")

    val properties = sgElb.get("Properties")
    properties.get("GroupName").to[String] must ===("sample-sg-elb")

    val egress = properties.get("SecurityGroupEgress").to[JsArray]
    egress.value.size must ===(1)
    egress(0).get("CidrIp").to[String] must ===("0.0.0.0/0")
    egress(0).get("IpProtocol").to[String] must ===("-1")

    val ingress = properties.get("SecurityGroupIngress").to[JsArray]
    ingress.value.size must ===(2)
    ingress(0).get("CidrIp").to[String] must ===("0.0.0.0/0")
    ingress(0).get("IpProtocol").to[String] must ===("tcp")
    ingress(0).get("FromPort").to[Int] must ===(80)
    ingress(0).get("ToPort").to[Int] must ===(80)
    ingress(1).get("CidrIp").to[String] must ===("0.0.0.0/0")
    ingress(1).get("IpProtocol").to[String] must ===("tcp")
    ingress(1).get("FromPort").to[Int] must ===(443)
    ingress(1).get("ToPort").to[Int] must ===(443)

    properties.get("VpcId") must ===(Json.obj("Ref" -> vpcId))
  }
}
