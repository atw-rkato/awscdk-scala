package com.myorg.lib

import software.amazon.awscdk.core.{ConstructNode, Stack}

import scala.util.{Failure, Try}

trait ContextReads[A] {
  def tryRead(value: Any): Try[A]
}

class CustomStack(val id: StackId, args: StackArgs) extends Stack(args.scope, id.value, args.props.orNull) {
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

abstract class StackWrapper(stack: CustomStack) {
  import scala.jdk.CollectionConverters.IterableHasAsScala

  lazy val id: StackId                           = stack.id
  lazy val artifactId: String                    = stack.getArtifactId
  lazy val stackName: String                     = stack.getStackName
  lazy val account: String                       = stack.getAccount
  lazy val availabilityZones: IndexedSeq[String] = stack.getAvailabilityZones.asScala.toIndexedSeq
}
