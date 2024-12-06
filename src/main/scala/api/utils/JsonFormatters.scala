package api.utils

import spray.json.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object JsonFormatters {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(uuid) => UUID.fromString(uuid)
      case _ => throw DeserializationException("UUID expected")
    }
  }

  implicit object DateTimeFormat extends JsonFormat[LocalDateTime] {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")

    def write(dateTime: LocalDateTime): JsValue = JsString(dateTime.format(formatter))
    def read(value: JsValue): LocalDateTime = value match {
      case JsString(dateTime) => LocalDateTime.parse(dateTime, formatter)
      case _ => throw DeserializationException("Datetime format 'yyyy-MM-dd HH:mm:ss.SSSSSSSSS' expected")
    }
  }

  implicit object OptionDateTimeFormat extends JsonFormat[Option[LocalDateTime]] {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")

    def write(dateTime: Option[LocalDateTime]): JsValue =
      val v = if dateTime.orNull == null then null else dateTime.get  
      JsString(v.format(formatter))

    def read(value: JsValue): Option[LocalDateTime] = value match {
      case JsString(dateTime) => Some(LocalDateTime.parse(dateTime, formatter))
      case _ => throw DeserializationException("Datetime format 'yyyy-MM-dd HH:mm:ss.SSSSSSSSS' expected")
    }
  }

  implicit object OptionStringFormat extends JsonFormat[Option[String]] {

    def write(value: Option[String]): JsValue =
      if value.orNull == null then null else JsString(value.get)

    def read(value: JsValue): Option[String] = value match {
      case JsString(value) => Some(value)
      case _ => throw DeserializationException("String expected")
    }
  }
}