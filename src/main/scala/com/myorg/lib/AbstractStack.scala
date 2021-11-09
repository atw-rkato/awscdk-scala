package com.myorg.lib

import software.amazon.awscdk.core
import software.amazon.awscdk.core.{Stack, StackProps}

import scala.util.{Failure, Try}

case class StackArgs(app: core.App, props: Option[StackProps] = None)

abstract class AbstractStack(val id: StackId, val args: StackArgs)
    extends Stack(args.app, id.value, args.props.orNull)
    with StackOps
    with StackFactory
    with CdkContext {
  lazy val app: core.App             = args.app
  lazy val props: Option[StackProps] = args.props
}

trait StackOps { self: AbstractStack =>
  private[this] lazy val node = getNode

  def tryGetContext[A: ContextReads](contextKey: String, default: => A): A =
    tryGetContext(contextKey)(implicitly[ContextReads[A]]).getOrElse(default)

  def tryGetContext[A: ContextReads](contextKey: String): Try[A] = {
    val ctx = node.tryGetContext(contextKey)
    if (ctx eq null) {
      Failure(new NoSuchElementException(s"context $contextKey doesn't exists."))
    } else {
      implicitly[ContextReads[A]].tryRead(ctx)
    }
  }
}

trait StackFactory {
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

trait ContextReads[A] {
  def tryRead(value: Any): Try[A]
}

trait CdkContext {
  implicit protected val ctxReadsForString: ContextReads[String] = value => Try(value.asInstanceOf[String])
}
