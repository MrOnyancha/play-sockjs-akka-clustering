package actors

import actors.UserSocket.WebSocketInit
import akka.actor.{Actor, ActorRef, Props}
import models.PlainMessage
import play.api.Logger

/**
  * Created by pritesh on 14/07/16.
  */
object WebSocket{

  def props(user:String,out:ActorRef,userSocket:ActorRef) =
    Props(classOf[WebSocket], user, out, userSocket)

  case class UserSetup(userId:String)
}
class WebSocket(userId:String, out:ActorRef, userRef:ActorRef) extends Actor {

  Logger.debug(s"$userId socket initiated" )


  override def preStart() = {
    Logger.debug(s"Sending self reference to owning user socket actor")
     userRef ! WebSocketInit(self)
  }

  override def receive: Receive = {
    case pm:PlainMessage =>
      Logger.debug(s"Received plain message from server $pm")
      out ! pm
  }
}
