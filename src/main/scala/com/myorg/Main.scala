package com.myorg

import com.myorg.example.VpcTemplateStack
import com.myorg.lib.{StackFactory, StackId}
import software.amazon.awscdk

import scala.collection.mutable

object Main {

  val stacks: IndexedSeq[StackFactory[_]] = IndexedSeq(
    VpcTemplateStack
  )

  def main(args: Array[String]): Unit = {
    requireNoDuplicateIds()

    val app = new awscdk.core.App

    stacks.foreach(_.apply(app))

    app.synth

    ()
  }

  /**
   * @throws IllegalStateException スタックIDが重複している場合
   */
  private def requireNoDuplicateIds(): Unit = {
    val errors = mutable.ListBuffer.empty[String]

    val ids = mutable.Set.empty[StackId]
    for (stack <- stacks) {
      val id = stack.id
      if (ids(id)) {
        errors += s"id重複: '${id.value}' of ${stack.getClass.getSimpleName}"
      } else {
        ids += id
      }
    }

    if (errors.nonEmpty) {
      throw new IllegalStateException(errors.mkString("\n"))
    }

    ()
  }
}
