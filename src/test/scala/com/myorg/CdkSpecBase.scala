package com.myorg

import com.myorg.lib.AbstractStack
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Assertions.fail
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.{JsNull, JsObject, JsValue, Json, Reads, Writes}

import scala.collection.immutable.ArraySeq
import scala.jdk.CollectionConverters.{IterableHasAsScala, MapHasAsScala}
import scala.reflect.{ClassTag, classTag}
import scala.util.control.NonFatal

abstract class CdkSpecBase extends AnyFreeSpec with TypeCheckedTripleEquals

object CdkSpecBase {
  import java.{lang => jl, math => jm, time => jt, util => ju}

  @annotation.nowarn
  private[this] implicit def BasicTypesWrites: Writes[Any] = {
    case v: JsValue => v
    case v: collection.Map[String, _] =>
      Json.toJson(v.map { case (k, v: Any) => (k, Json.toJson(v)(BasicTypesWrites)) })
    case v: ju.Map[String, _]      => Json.toJson(v.asScala: Any)(BasicTypesWrites)
    case v: collection.Iterable[_] => Json.toJson(v.map(Json.toJson(_: Any)(BasicTypesWrites)))
    case v: jl.Iterable[_]         => Json.toJson(v.asScala: Any)(BasicTypesWrites)
    case v: Array[_]               => Json.toJson(ArraySeq.unsafeWrapArray(v): Any)(BasicTypesWrites)
    case v: Int                    => Json.toJson(v)
    case v: Short                  => Json.toJson(v)
    case v: Byte                   => Json.toJson(v)
    case v: Long                   => Json.toJson(v)
    case v: Float                  => Json.toJson(v)
    case v: Double                 => Json.toJson(v)
    case v: BigDecimal             => Json.toJson(v)
    case v: BigInt                 => Json.toJson(v)
    case v: jm.BigInteger          => Json.toJson(v)
    case v: Boolean                => Json.toJson(v)
    case v: String                 => Json.toJson(v)
    case v: Char                   => Json.toJson(String.valueOf(v))
    case v: ju.Date                => Json.toJson(v)
    case v: jt.LocalDate           => Json.toJson(v)
    case v: jt.LocalDateTime       => Json.toJson(v)
    case v: jt.LocalTime           => Json.toJson(v)
    case v: jt.OffsetDateTime      => Json.toJson(v)
    case v: jt.ZonedDateTime       => Json.toJson(v)
    case v: jt.Instant             => Json.toJson(v)
    case v: ju.UUID                => Json.toJson(v)
    case v: Some[_]                => Json.toJson(v.get: Any)(BasicTypesWrites)
    case None                      => JsNull
    case v if v == null            => JsNull
    case _                         => throw new UnsupportedOperationException
  }

  implicit class TestStackOps(val value: AbstractStack) extends AnyVal {
    def toJson: JsValue = {
      val template = value.app.synth.getStackArtifact(value.getArtifactId).getTemplate
      Json.toJson(template: Any)
    }
  }

  implicit class TestJsValueOps(val value: JsValue) extends AnyVal {
    def get(fieldName: String): JsValue = value match {
      case _: JsObject =>
        try {
          value.apply(fieldName)
        } catch {
          case NonFatal(_) => fail(s"'$fieldName' is undefined in \n${Json.prettyPrint(value)}")
        }
      case _ => fail(s"cannot get '$fieldName' because ${Json.prettyPrint(value)} is not a JsObject")
    }

    def to[A: ClassTag](implicit fjs: Reads[A]): A = {
      value.validate.fold(
        err => {
          val errors = err.flatMap(_._2).flatMap(_.messages)
          val msg =
            s"${value.getClass.getSimpleName}($value) cannot convert to ${classTag[A].runtimeClass.getSimpleName}"

          fail(errors.mkString("", "\n", "\n") + msg)
        },
        identity,
      )
    }
  }
}
