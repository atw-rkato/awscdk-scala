package com.myorg.example

import com.myorg.example.context.ContextKeys

import java.{util => ju}
import scala.jdk.CollectionConverters.MapHasAsJava

object TestProps {
  val Context: ju.Map[String, Any] = {
    val c = Map[String, Any](
      ContextKeys.KeyName -> "testKeyName"
    )

    ju.Collections.unmodifiableMap(c.asJava)
  }
}
