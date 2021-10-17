package com.myorg.lib

import software.amazon.awscdk.core
import software.amazon.awscdk.core.StackProps

case class StackArgs(app: core.App, props: Option[StackProps] = None)
