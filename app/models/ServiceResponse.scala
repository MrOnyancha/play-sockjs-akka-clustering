package models

import play.api.libs.json.JsObject

/**
  * Created by pritesh on 17/07/16.
  */
case class ServiceResponse(path:String, body:JsObject, key:String)
