package com.myorg.lib

import software.amazon.awscdk.core
import software.amazon.awscdk.core.{ScopedAws, Stack, StackProps}

import scala.util.{Failure, Try}

case class StackArgs(app: core.App, props: Option[StackProps] = None)

case class MyStack(id: StackId, stackArgs: StackArgs)
    extends Stack(stackArgs.app, id.value, stackArgs.props.orNull)
    with StackOps {

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
  val app: core.App             = stack.stackArgs.app
  val props: Option[StackProps] = stack.stackArgs.props

  lazy val artifactId: String = stack.getArtifactId
}
