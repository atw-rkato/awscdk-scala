package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
import software.amazon.awscdk.core.ScopedAws
import software.amazon.awscdk.services.iam.{CfnInstanceProfile, ManagedPolicy, Role, ServicePrincipal}

object SampleIamStack {
  val id: StackId = StackId("iam-stack")
}

class SampleIamStack(args: StackArgs) extends AbstractStack(SampleIamStack.id, args) {

  val webRole: Role = {
    val urlSuffix = scopedAws.getUrlSuffix

    val roleName = "sample-role-web"
    val webRole = Role.Builder
      .create(this, "SampleS3Role")
      .assumedBy(ServicePrincipal.Builder.create(s"ec2.${urlSuffix}").build())
      .managedPolicies(jList(ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")))
      .roleName(roleName)
      .description("upload images")
      .build()

    CfnInstanceProfile.Builder
      .create(this, "SampleS3RoleInstanceProfile")
      .instanceProfileName(roleName)
      .roles(jList(roleName))
      .build()

    webRole
  }
}
