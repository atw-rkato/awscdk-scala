package com.myorg

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Assertions.fail
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import software.amazon.awscdk
import software.amazon.awscdk.core.Stack

import scala.jdk.CollectionConverters.{IterableHasAsScala, MapHasAsScala}
import scala.reflect.{ClassTag, classTag}
import scala.util.control.NonFatal

abstract class CdkSpecBase extends AnyFunSuite with Matchers with TypeCheckedTripleEquals {
  protected def createTemplate(app: awscdk.core.App, stack: Stack): JsValue = CdkSpecBase.createTemplate(app, stack)
}

object CdkSpecBase {

  def createTemplate(app: awscdk.core.App, stack: Stack): JsValue = {
    val template = app.synth.getStackArtifact(stack.getArtifactId).getTemplate

    Json.toJson(template: Any)
  }

  @annotation.nowarn
  implicit lazy val BasicTypesWrites: Writes[Any] = {
    case v: JsValue => v
    case v: collection.Map[String, _] =>
      Json.toJson(v.map { case (k, v: Any) => (k, Json.toJson(v)(BasicTypesWrites)) })
    case v: java.util.Map[String, _] => Json.toJson(v.asScala: Any)(BasicTypesWrites)
    case v: collection.Iterable[_]   => Json.toJson(v.map(Json.toJson(_: Any)(BasicTypesWrites)))
    case v: java.lang.Iterable[_]    => Json.toJson(v.asScala: Any)(BasicTypesWrites)
    case v: String                   => Json.toJson(v)
    case v: Boolean                  => Json.toJson(v)
    case v: Byte                     => Json.toJson(v)
    case v: Short                    => Json.toJson(v)
    case v: Int                      => Json.toJson(v)
    case v: Long                     => Json.toJson(v)
    case v: Float                    => Json.toJson(v)
    case v: Double                   => Json.toJson(v)
    case v: Char                     => Json.toJson(String.valueOf(v))
    case v: BigInt                   => Json.toJson(v)
    case v: BigDecimal               => Json.toJson(v)
    case v: java.util.Date           => Json.toJson(v)
    case v: java.time.LocalDate      => Json.toJson(v)
    case v: java.time.LocalDateTime  => Json.toJson(v)
    case v: java.time.LocalTime      => Json.toJson(v)
    case v: java.time.OffsetDateTime => Json.toJson(v)
    case v: java.time.ZonedDateTime  => Json.toJson(v)
    case v: java.time.Instant        => Json.toJson(v)
    case v if v == null              => JsNull
    case _                           => throw new UnsupportedOperationException
  }
}

object TestOps {
  implicit class JsValueOps(val value: JsValue) extends AnyVal {
    def get(fieldName: String): JsValue = value match {
      case _: JsObject =>
        try {
          value.apply(fieldName)
        } catch {
          case NonFatal(_) => fail(s"'$fieldName' is undefined on object: ${Json.prettyPrint(value)}")
        }
      case _ => fail(s"cannot get '$fieldName' because ${Json.prettyPrint(value)} is not a JsObject")
    }

    def getAs[A: ClassTag](fieldName: String)(implicit fjs: Reads[A]): A = {
      val value = get(fieldName)
      value.validate.fold(
        err => {
          val errors = err.flatMap(_._2).flatMap(_.messages)
          fail(errors.mkString("", "\n", "\n") + s"'$value' cannot convert to ${classTag[A].runtimeClass}")
        },
        identity,
      )
    }
  }
}
