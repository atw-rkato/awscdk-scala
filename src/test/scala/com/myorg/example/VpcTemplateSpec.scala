package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.{JsValueOps, StackWrapperOps}
import com.myorg.lib.StackArgs
import play.api.libs.json.{JsArray, Json}
import software.amazon.awscdk.core

class VpcTemplateSpec extends CdkSpecBase {

  val vpcId       = "SampleVpc07DAD426"
  val sgBastionId = "SampleSgBastion1679A31B"
  val sgElbId     = "SampleSgElb3916B66D"

  test("test VPC") {
    val vpcStack = VpcStack(StackArgs(new core.App))
    val template = vpcStack.toJson

    val vpc = template.get("Resources").get(vpcId)
    vpc.get("Type").to[String] should ===("AWS::EC2::VPC")

    val properties = vpc.get("Properties")
    properties.get("CidrBlock").to[String] should ===("10.0.0.0/16")
    properties.get("EnableDnsHostnames").to[Boolean] should ===(true)
    properties.get("EnableDnsSupport").to[Boolean] should ===(true)
    properties.get("InstanceTenancy").to[String] should ===("default")
  }

  test("test SecurityGroup for bastion server") {
    val vpcStack = VpcStack(StackArgs(new core.App))
    val template = vpcStack.toJson

    val sgBastion = template.get("Resources").get(sgBastionId)
    sgBastion.get("Type").to[String] should ===("AWS::EC2::SecurityGroup")

    val properties = sgBastion.get("Properties")
    properties.get("GroupName").to[String] should ===("sample-sg-bastion")

    val egress = properties.get("SecurityGroupEgress").to[JsArray]
    egress.value.size should ===(1)
    egress(0).get("CidrIp").to[String] should ===("0.0.0.0/0")
    egress(0).get("IpProtocol").to[String] should ===("-1")

    val ingress = properties.get("SecurityGroupIngress").to[JsArray]
    ingress.value.size should ===(1)
    ingress(0).get("CidrIp").to[String] should ===("0.0.0.0/0")
    ingress(0).get("IpProtocol").to[String] should ===("tcp")
    ingress(0).get("FromPort").to[Int] should ===(22)
    ingress(0).get("ToPort").to[Int] should ===(22)

    properties.get("VpcId") should ===(Json.obj("Ref" -> vpcId))
  }

  test("test SecurityGroup for  load balancer") {
    val vpcStack = VpcStack(StackArgs(new core.App))
    val template = vpcStack.toJson

    val sgElb = template.get("Resources").get(sgElbId)
    sgElb.get("Type").to[String] should ===("AWS::EC2::SecurityGroup")

    val properties = sgElb.get("Properties")
    properties.get("GroupName").to[String] should ===("sample-sg-elb")

    val egress = properties.get("SecurityGroupEgress").to[JsArray]
    egress.value.size should ===(1)
    egress(0).get("CidrIp").to[String] should ===("0.0.0.0/0")
    egress(0).get("IpProtocol").to[String] should ===("-1")

    val ingress = properties.get("SecurityGroupIngress").to[JsArray]
    ingress.value.size should ===(2)
    ingress(0).get("CidrIp").to[String] should ===("0.0.0.0/0")
    ingress(0).get("IpProtocol").to[String] should ===("tcp")
    ingress(0).get("FromPort").to[Int] should ===(80)
    ingress(0).get("ToPort").to[Int] should ===(80)
    ingress(1).get("CidrIp").to[String] should ===("0.0.0.0/0")
    ingress(1).get("IpProtocol").to[String] should ===("tcp")
    ingress(1).get("FromPort").to[Int] should ===(443)
    ingress(1).get("ToPort").to[Int] should ===(443)

    properties.get("VpcId") should ===(Json.obj("Ref" -> vpcId))
  }
}
