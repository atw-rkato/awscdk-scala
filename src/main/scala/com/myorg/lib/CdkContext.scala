package com.myorg.lib

import scala.util.Try

trait CdkContext {
  implicit protected val ctxReadsForString: ContextReads[String] = value => Try(value.asInstanceOf[String])
}
