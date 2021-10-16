package com.myorg.lib

import software.amazon.awscdk.core.{IConstruct, Stack, StackProps}

import scala.util.{Failure, Try}

abstract class StackBase(protected val id: StackId, args: StackArgs)
    extends Stack(args.scope, id.value, args.props.orNull)
    with StackOps {
  protected val scope: IConstruct         = args.scope
  protected val props: Option[StackProps] = args.props

  private lazy val node = getNode

  protected def tryGetContext[A: ContextReads](contextKey: String): Try[A] = {
    val ctx = node.tryGetContext(contextKey)
    if (ctx eq null) {
      Failure(new NoSuchElementException(s"context $contextKey doesn't exists."))
    } else {
      implicitly[ContextReads[A]].tryRead(ctx)
    }
  }
}

trait StackOps {
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

trait ContextReads[A] {
  def tryRead(value: Any): Try[A]
}
