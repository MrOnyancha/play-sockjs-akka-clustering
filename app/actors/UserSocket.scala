package actors

import actors.UserSocket.{ConnectionRequest, WebSocketInit}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.ActorMaterializer
import models.{PlainMessage, ServiceResponse}
import play.api.{Configuration, Logger}
import play.api.libs.streams.ActorFlow
import akka.pattern.{ask, pipe}
import javax.inject.Inject
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random


/**
  * Created by Onyancha on 16/07/16.
  */
object UserSocket extends ShardedFunctions{

  def props (implicit  ec: ExecutionContext, configuration: Configuration) = Props[UserSocket]

  case class ConnectionRequest(userId:String)
  case class WebSocketInit(userId:String, ref:ActorRef)

}
class UserSocket(implicit  ec: ExecutionContext, configuration: Configuration) extends Actor with ActorLogging{

  var socket:Option[ActorRef]  = None
  val random = new Random()
  val numberOfDevices = 50

  val reciever  =  configuration.get[String](UserSocket.playHostKey).toString

  override def receive: Receive = {

    case ConnectionRequest(userId) =>

      Logger.debug("Connection request received creating new websocket")

    case ws:WebSocketInit =>
      val deviceId = random.nextInt(numberOfDevices)
      val temperature = 5 + 30 * random.nextDouble()
      val msg =  ServiceResponse(s"heres/test$deviceId", Json.obj("value" -> temperature), s"user$deviceId")
      Logger.debug(s"Sending $msg");
      ws.ref !  msg

    case sr:ServiceResponse =>
      Logger.debug(s"Received service response $reciever")
      Logger info s"Recording ${sr.key} for device ${sr.path}, = ${sr.body}"
      Logger info s"MY PATH is ${context.sender.path}"
//      Logger.debug(socket.toString)
//      val reply = sender()
//      socket match {
//        case Some(ws) =>
//          Logger.debug("Sending service response to web socket")
//          ws ! PlainMessage(sr.path,sr.body)
//          reply ! Right("Message sent to websocket")
//        case None =>
//          Logger.debug("No websocket link")
//          reply ! Left("Socket not associated with user socket")
//      }
  }
}
