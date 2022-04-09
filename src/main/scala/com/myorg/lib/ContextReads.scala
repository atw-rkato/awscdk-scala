package com.myorg.lib

import scala.util.Try

trait ContextReads[A] {
  def tryRead(value: Any): Try[A]
}
