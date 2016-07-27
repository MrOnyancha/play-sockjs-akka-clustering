package actors

import actors.UserSocket.{ConnectionRequest, WebSocketInit}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.ActorMaterializer
import models.{PlainMessage, ServiceResponse}
import play.api.Logger
import play.api.libs.streams.ActorFlow
import akka.pattern.{ask, pipe}

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by pritesh on 16/07/16.
  */
object UserSocket extends ShardedFunctions{

  def props = Props[UserSocket]

  case class ConnectionRequest(userId:String)
  case class WebSocketInit(userId:String,ref:ActorRef)





}
class UserSocket extends Actor with ActorLogging{

  var socket:Option[ActorRef]  = None



  override def receive: Receive = {

    case ConnectionRequest(userId) =>
      Logger.debug("Connection request received creating new websocket")

    case ws:WebSocketInit =>
      Logger.debug(s"Websocket now linked with user socket $ws")
      socket = Some(ws.ref)
      Logger.debug(socket.toString)

    case sr:ServiceResponse =>
      Logger.debug("Received service response")
      Logger.debug(socket.toString)
      val reply = sender()
      socket match {
        case Some(ws) =>
          Logger.debug("Sending service response to web socket")
          ws ! PlainMessage(sr.path,sr.body)
          reply ! Right("Message sent to websocket")
        case None =>
          Logger.debug("No websocket link")
          reply ! Left("Socket not associated with user socket")
      }



  }
}
