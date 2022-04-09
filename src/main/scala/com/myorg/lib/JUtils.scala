package com.myorg.lib

trait JUtils {

  import java.util as ju

  protected def jList[A](elems: A*): ju.List[A] = ju.List.of(elems: _*)

  protected def jMap[K, V](elems: (K, V)*): ju.Map[K, V] = {
    val m = new ju.HashMap[K, V](elems.size)
    for ((key, value) <- elems) {
      m.put(key, value)
    }

    ju.Collections.unmodifiableMap(m)
  }
}
