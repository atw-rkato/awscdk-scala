package com.myorg.lib

import software.amazon.awscdk.core.{Construct, Stack, StackProps}

abstract class StackBase(scope: Construct, id: StackId, props: Option[StackProps] = None)
    extends Stack(scope, id.value, props.orNull)
    with StackOps

trait StackOps {
  import java.{util => ju}

  protected def jList[A](elems: A*): ju.List[A] = ju.List.of(elems: _*)

  protected def jMap[K, V](elems: (K, V)*): ju.Map[K, V] = {
    val m = new ju.HashMap[K, V](elems.size)
    for ((key, value) <- elems) {
      m.put(key, value)
    }

    ju.Collections.unmodifiableMap(m)
  }
}
