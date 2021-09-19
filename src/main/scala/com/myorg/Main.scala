package com.myorg

import com.myorg.sample.VpcTemplateStack
import software.amazon.awscdk

object Main {
  def main(args: Array[String]): Unit = {
    val app = new awscdk.core.App

    new VpcTemplateStack(app, "VpcTemplate")

    app.synth

    ()
  }
}
