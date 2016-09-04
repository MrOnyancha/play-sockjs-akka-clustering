package models

import play.api.libs.json.{JsObject, Json}

/**
  * Created by pritesh on 17/07/16.
  */
case class ServiceResponse(path:String, body:JsObject, key:String)

object ServiceResponse{
  implicit val format = Json.format[ServiceResponse]
}
