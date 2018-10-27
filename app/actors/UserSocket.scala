package actors

import actors.UserSocket.{ConnectionRequest, WebSocketInit}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import models.ServiceResponse
import play.api.libs.json.Json
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext
import scala.util.Random


/**
  * Created by Onyancha on 16/07/16.
  */
object UserSocket extends ShardedFunctions {

  def props(implicit ec: ExecutionContext, configuration: Configuration) = Props(new UserSocket(ec, configuration))

  case class ConnectionRequest(userId: String)

  case class WebSocketInit(userId: String, ref: ActorRef)

}

class UserSocket(e: ExecutionContext, c: Configuration) extends Actor with ActorLogging {
  implicit val ec: ExecutionContext = e
  implicit val configuration: Configuration = c

  var socket: Option[ActorRef] = None
  val random = new Random()
  val numberOfDevices = 5000

  val reciever = configuration.get[String](UserSocket.playHostKey).toString

  override def receive: Receive = {

    case ConnectionRequest(userId) =>

      Logger.debug("Connection request received creating new websocket")

    case ws: WebSocketInit =>
      val deviceId = random.nextInt(numberOfDevices)
      val temperature = 5 + 30 * random.nextDouble()
      val msg = ServiceResponse(s"heres/test$deviceId", Json.obj("value" -> temperature), s"user$deviceId")
      Logger.debug(s"Sending $msg");
      ws.ref ! msg

    case sr: ServiceResponse =>
      Logger.debug(s"Received service response: $reciever")
      Logger info s"Recording ${sr.key} for device ${sr.path}, = ${sr.body}"
      Logger info s"MY PATH is ${context.sender.path}"
//      context.stop(self)
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
