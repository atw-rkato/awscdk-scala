package com.myorg.sample

import software.amazon.awscdk.core.{Construct, Stack, StackProps}

abstract class StackBase(scope: Construct, id: String, props: Option[StackProps] = None)
    extends Stack(scope, id, props.orNull) {

  def this(scope: Construct, id: String, props: StackProps) = {
    this(scope, id, Some(props))
  }
}
