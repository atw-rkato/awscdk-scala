package com.myorg.lib

import software.amazon.awscdk.core.{Construct, StackProps}

case class StackArgs(scope: Construct, props: Option[StackProps] = None)
