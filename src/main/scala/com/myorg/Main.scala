package com.myorg

import com.myorg.example.VpcTemplateStack
import com.myorg.lib.{StackFactory, StackId}
import software.amazon.awscdk

import scala.collection.mutable

object Main {

  val stacks: Seq[StackFactory[_]] = Seq(
    VpcTemplateStack
  )

  def main(args: Array[String]): Unit = {
    checkStacks()

    val app = new awscdk.core.App

    stacks.foreach(stack => stack(app))

    app.synth

    ()
  }

  private def checkStacks(): Unit = {
    val ids = mutable.Set.empty[StackId]
    for (stack <- stacks) {
      val id = stack.id
      if (ids(id)) {
        throw new IllegalStateException(s"id重複: '${id.value}' of ${stack.getClass.getSimpleName}")
      }
      ids += id
    }

    ()
  }
}
