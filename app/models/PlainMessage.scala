package models

import play.api.libs.json.{JsObject, Json}


/**
  * Created by pritesh on 16/07/16.
  */
case class PlainMessage(path:String, body:JsObject)

object PlainMessage {
  implicit val formats = Json.format[PlainMessage]
}
