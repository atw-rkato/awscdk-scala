package com.myorg.example

import com.myorg.CdkSpecBase
import com.myorg.CdkSpecBase.{TestJsValueOps, TestStackOps}
import com.myorg.lib.StackArgs
import play.api.libs.json.{JsArray, JsValue, Json}
import software.amazon.awscdk.core
import software.amazon.awscdk.core.AppProps

object SampleVpcStackSpec {
  val VpcId                  = "SampleVpc07DAD426"
  val PubSubnet1Id           = "SampleVpcPublicSubnet1Subnet365106BA"
  val PubSubnet1RtbId        = "SampleVpcPublicSubnet1RouteTableE2EAB2A8"
  val PubSubnet1RtbAssocId   = "SampleVpcPublicSubnet1RouteTableAssociation77BCAA2F"
  val PubSubnet1RouteId      = "SampleVpcPublicSubnet1DefaultRouteEA6D638D"
  val PubSubnet1EIPId        = "SampleVpcPublicSubnet1EIPF08B2590"
  val PubSubnet1NatGatewayId = "SampleVpcPublicSubnet1NATGateway12B23DA1"
  val InternetGateWayId      = "SampleVpcIGW8FA8DC37"
  val GatewayAttachmentId    = "SampleVpcVPCGW2B38F286"
  val SgBastionId            = "SampleSgBastion1679A31B"
  val SgElbId                = "SampleSgElb3916B66D"

  private def getTemplate(context: java.util.Map[String, Any] = TestProps.Context): JsValue = {
    val stackArgs = StackArgs(new core.App(AppProps.builder().context(context).build()))
    val vpcStack  = SampleVpcStack(stackArgs)
    vpcStack.toJson
  }
}

class SampleVpcStackSpec extends CdkSpecBase {

  import SampleVpcStackSpec.*

  "VPC" - {
    "env test" in {
      val template = getTemplate()

      val resource = template.get("Resources").get(VpcId)
      assert(resource.get("Type").to[String] === "AWS::EC2::VPC")

      val properties = resource.get("Properties")
      assert(properties.get("CidrBlock").to[String] === "10.0.0.0/16")
      assert(properties.get("EnableDnsHostnames").to[Boolean] === true)
      assert(properties.get("EnableDnsSupport").to[Boolean] === true)
      assert(properties.get("InstanceTenancy").to[String] === "default")
      val tags = properties.get("Tags").to[JsArray]
      assert(tags.value.size === 1)
      assert(tags(0) === Json.obj("Key" -> "Name", "Value" -> "vpc-stack/SampleVpc"))
    }
  }

  "PublicSubnet1" - {
    "env test" - {
      val template = getTemplate()

      "Subnet" in {
        val resource = template.get("Resources").get(PubSubnet1Id)
        assert(resource.get("Type").to[String] === "AWS::EC2::Subnet")

        val properties = resource.get("Properties")
        assert(properties.get("CidrBlock").to[String] === "10.0.0.0/18")
        assert(properties.get("VpcId") === Json.obj("Ref" -> VpcId))
        assert(
          properties.get("AvailabilityZone") === Json.obj(
            "Fn::Select" -> Json.arr(0, Json.obj("Fn::GetAZs" -> ""))
          )
        )
        assert(properties.get("MapPublicIpOnLaunch").to[Boolean] === true)
        val tags = properties.get("Tags").to[JsArray]
        assert(tags.value.size === 3)
        assert(tags(0) === Json.obj("Key" -> "aws-cdk:subnet-name", "Value" -> "Public"))
        assert(tags(1) === Json.obj("Key" -> "aws-cdk:subnet-type", "Value" -> "Public"))
        assert(tags(2) === Json.obj("Key" -> "Name", "Value" -> "vpc-stack/SampleVpc/PublicSubnet1"))
      }

      "RouteTable" in {
        val resource = template.get("Resources").get(PubSubnet1RtbId)
        assert(resource.get("Type").to[String] === "AWS::EC2::RouteTable")

        val properties = resource.get("Properties")
        assert(properties.get("VpcId") === Json.obj("Ref" -> VpcId))
        val tags = properties.get("Tags").to[JsArray]
        assert(tags.value.size === 1)
        assert(tags(0) === Json.obj("Key" -> "Name", "Value" -> "vpc-stack/SampleVpc/PublicSubnet1"))
      }

      "SubnetRouteTableAssociation" in {
        val resource = template.get("Resources").get(PubSubnet1RtbAssocId)
        assert(resource.get("Type").to[String] === "AWS::EC2::SubnetRouteTableAssociation")

        val properties = resource.get("Properties")
        assert(properties.get("RouteTableId") === Json.obj("Ref" -> PubSubnet1RtbId))
        assert(properties.get("SubnetId") === Json.obj("Ref" -> PubSubnet1Id))
      }

      "Route" in {
        val resource = template.get("Resources").get(PubSubnet1RouteId)
        assert(resource.get("Type").to[String] === "AWS::EC2::Route")

        val properties = resource.get("Properties")
        assert(properties.get("RouteTableId") === Json.obj("Ref" -> PubSubnet1RtbId))
        assert(properties.get("DestinationCidrBlock").to[String] === "0.0.0.0/0")
        assert(properties.get("GatewayId") === Json.obj("Ref" -> InternetGateWayId))

        assert(resource.get("DependsOn") === Json.arr(GatewayAttachmentId))
      }

      "EIP" in {
        val resource = template.get("Resources").get(PubSubnet1EIPId)
        assert(resource.get("Type").to[String] === "AWS::EC2::EIP")

        val properties = resource.get("Properties")
        assert(properties.get("Domain").to[String] === "vpc")
        val tags = properties.get("Tags").to[JsArray]
        assert(tags.value.size === 1)
        assert(tags(0) === Json.obj("Key" -> "Name", "Value" -> "vpc-stack/SampleVpc/PublicSubnet1"))
      }

      "NatGateway" in {
        val resource = template.get("Resources").get(PubSubnet1NatGatewayId)
        assert(resource.get("Type").to[String] === "AWS::EC2::NatGateway")

        val properties = resource.get("Properties")
        assert(properties.get("SubnetId") === Json.obj("Ref" -> PubSubnet1Id))
        assert(properties.get("AllocationId") === Json.obj("Fn::GetAtt" -> Json.arr(PubSubnet1EIPId, "AllocationId")))
        val tags = properties.get("Tags").to[JsArray]
        assert(tags.value.size === 1)
        assert(tags(0) === Json.obj("Key" -> "Name", "Value" -> "vpc-stack/SampleVpc/PublicSubnet1"))
      }
    }
  }

  "InternetGateway" - {
    "env test" in {
      val template = getTemplate()
      val resource = template.get("Resources").get(InternetGateWayId)
      assert(resource.get("Type").to[String] === "AWS::EC2::InternetGateway")

      val properties = resource.get("Properties")
      val tags       = properties.get("Tags").to[JsArray]
      assert(tags.value.size === 1)
      assert(tags(0) === Json.obj("Key" -> "Name", "Value" -> "vpc-stack/SampleVpc"))
    }
  }

  "VPCGatewayAttachment" - {
    "env test" in {
      val template = getTemplate()
      val resource = template.get("Resources").get(GatewayAttachmentId)
      assert(resource.get("Type").to[String] === "AWS::EC2::VPCGatewayAttachment")

      val properties = resource.get("Properties")
      assert(properties.get("VpcId") === Json.obj("Ref" -> VpcId))
      assert(properties.get("InternetGatewayId") === Json.obj("Ref" -> InternetGateWayId))
      assert(properties.has("Tags") === false)
    }
  }

  "SecurityGroup for bastion server" - {
    "env test" in {
      val template = getTemplate()

      val resource = template.get("Resources").get(SgBastionId)
      assert(resource.get("Type").to[String] === "AWS::EC2::SecurityGroup")

      val properties = resource.get("Properties")
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

      assert(properties.get("VpcId") === Json.obj("Ref" -> VpcId))
    }
  }

  "SecurityGroup for load balancer" - {
    "env test" in {
      val template = getTemplate()

      val resource = template.get("Resources").get(SgElbId)
      assert(resource.get("Type").to[String] === "AWS::EC2::SecurityGroup")

      val properties = resource.get("Properties")
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

      assert(properties.get("VpcId") === Json.obj("Ref" -> VpcId))
    }
  }
}
