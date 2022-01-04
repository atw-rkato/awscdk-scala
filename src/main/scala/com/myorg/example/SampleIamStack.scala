package com.myorg.example

import com.myorg.lib.{AbstractStack, StackArgs, StackId}
import software.amazon.awscdk.core.ScopedAws
import software.amazon.awscdk.services.iam.{CfnInstanceProfile, ManagedPolicy, Role, ServicePrincipal}

object SampleIamStack {
  val id: StackId = StackId("iam-stack")
}

class SampleIamStack(args: StackArgs) extends AbstractStack(SampleIamStack.id, args) {

  val webRole: Role = {
    val scopedAws = new ScopedAws(this)
    val urlSuffix = scopedAws.getUrlSuffix

    val webRole = Role.Builder
      .create(this, "SampleS3Role")
      .assumedBy(ServicePrincipal.Builder.create(s"ec2.${urlSuffix}").build())
      .managedPolicies(
        jList(
          ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")
        )
      )
      .roleName("sample-role-web")
      .description("upload images")
      .build()

    CfnInstanceProfile.Builder
      .create(this, "SampleS3RoleInstanceProfile")
      .instanceProfileName(webRole.getRoleName)
      .roles(jList(webRole.getRoleName))
      .build()

    webRole
  }
}
