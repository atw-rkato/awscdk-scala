package com.myorg.lib

import software.amazon.awscdk.core
import software.amazon.awscdk.core.{ConstructNode, Stack, StackProps}

import scala.util.{Failure, Try}

trait ContextReads[A] {
  def tryRead(value: Any): Try[A]
}

case class StackArgs(app: core.App, props: Option[StackProps] = None)

abstract class AbstractStack(val id: StackId, val args: StackArgs)
    extends Stack(args.app, id.value, args.props.orNull)
    with StackFactory {

  lazy val app: core.App             = args.app
  lazy val props: Option[StackProps] = args.props

  private lazy val node: ConstructNode = getNode

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
  implicit protected val ctxReadsForString: ContextReads[String] = value => Try(value.asInstanceOf[String])

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
