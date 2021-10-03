package com.myorg.lib

import software.amazon.awscdk.core.{Construct, Stack, StackProps}

trait StackFactory[A <: Stack] {
  def id: StackId

  def apply(scope: Construct, props: Option[StackProps] = None): A

  def apply(scope: Construct, props: StackProps): A = apply(scope, Some(props))
}
