package com.myorg.lib

import software.amazon.awscdk.core
import software.amazon.awscdk.core.{ScopedAws, Stack, StackProps}

import scala.sys.props
import scala.util.{Failure, Try}

case class StackContext(app: core.App, props: Option[StackProps] = None)

case class MyStack(id: StackId)(implicit ctx: StackContext)
    extends Stack(ctx.app, id.value, ctx.props.orNull)
    with StackOps {
  val app: core.App             = ctx.app
  val props: Option[StackProps] = ctx.props

  lazy val scopedAws: ScopedAws = new ScopedAws(this)
}

trait StackOps {
  self: MyStack =>
  private lazy val node = getNode

  def tryGetContext[A: ContextReads](contextKey: String, default: => A): A =
    tryGetContext(contextKey)(implicitly[ContextReads[A]]).getOrElse(default)

  def tryGetContext[A: ContextReads](contextKey: String): Try[A] = {
    val ctx = node.tryGetContext(contextKey)
    if (ctx == "undefined") {
      Failure(new NoSuchElementException(s"context $contextKey doesn't exists."))
    } else {
      implicitly[ContextReads[A]].tryRead(ctx)
    }
  }
}

abstract class StackWrapper(stack: MyStack) {
  val id: StackId               = stack.id
  val app: core.App             = stack.app
  val props: Option[StackProps] = stack.props

  lazy val artifactId: String = stack.getArtifactId
}
