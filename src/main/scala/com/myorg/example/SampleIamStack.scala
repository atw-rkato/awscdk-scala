package com.myorg.example

import com.myorg.lib.{MyStack, StackArgs, StackFactory, StackId, StackWrapper}
import software.amazon.awscdk.services.iam.{CfnInstanceProfile, ManagedPolicy, Role, ServicePrincipal}

object SampleIamStack extends StackFactory {
  val id: StackId = StackId("iam-stack")

  def apply(stackArgs: StackArgs): SampleIamStack = {
    val stack     = MyStack(SampleIamStack.id, stackArgs)
    val urlSuffix = stack.scopedAws.getUrlSuffix

    val roleName = "sample-role-web"
    val webRole = Role.Builder
      .create(stack, "SampleS3Role")
      .assumedBy(ServicePrincipal.Builder.create(s"ec2.${urlSuffix}").build())
      .managedPolicies(jList(ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")))
      .roleName(roleName)
      .description("upload images")
      .build()

    CfnInstanceProfile.Builder
      .create(stack, "SampleS3RoleInstanceProfile")
      .instanceProfileName(roleName)
      .roles(jList(roleName))
      .build()

    new SampleIamStack(stack, webRole)
  }
}

class SampleIamStack private (stack: MyStack, val webRole: Role) extends StackWrapper(stack)
