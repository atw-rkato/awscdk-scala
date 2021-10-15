package com.myorg.lib

import software.amazon.awscdk.core.{Construct, Stack, StackProps}

import scala.util.{Failure, Try}

abstract class StackBase(protected val id: StackId, args: StackArgs)
    extends Stack(args.scope, id.value, args.props.orNull)
    with StackOps {
  protected val scope: Construct          = args.scope
  protected val props: Option[StackProps] = args.props

  protected def tryGetContext[A: ContextRead](contextKey: String): Try[A] = {
    val ctx = getNode.tryGetContext(contextKey)
    if (ctx eq null) {
      Failure(new NoSuchElementException(s"context $contextKey doesn't exists."))
    } else {
      implicitly[ContextRead[A]].tryRead(ctx)
    }
  }
}

trait StackOps {
  implicit val contextReadString: ContextRead[String] = value => Try(value.asInstanceOf[String])

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

trait ContextRead[A] {
  def tryRead(value: Any): Try[A]
}
