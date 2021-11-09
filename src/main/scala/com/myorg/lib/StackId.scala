package com.myorg.lib

import java.util.regex.Pattern

case class StackId(value: String) {
  import com.myorg.lib.StackId.pattern

  if (!pattern.matcher(value).matches()) {
    throw new IllegalStateException(s"id不正: '$value'")
  }
}

object StackId {
  private lazy val regex   = """^[A-Za-z][A-Za-z0-9-]+$"""
  private lazy val pattern = Pattern.compile(regex)
}
