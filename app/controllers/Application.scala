package controllers

import actors.{UserSocket, WebSocket}
import actors.UserSocket.ConnectionRequest
import akka.actor.{ActorNotFound, ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.sockjs.api.{SockJS, SockJSRouter}
import akka.pattern.{AskTimeoutException, ask}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import models.{PlainMessage, ServiceResponse}
import play.api.Logger
import play.api.libs.streams.ActorFlow
import play.sockjs.api.SockJS.MessageFlowTransformer

import scala.concurrent.duration._
import scala.util.{Failure, Right, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Application @Inject() (implicit system:ActorSystem) extends Controller with SockJSRouter {

  type SockJsFlow = Flow[JsValue,JsValue,_]

  implicit val timeout:Timeout = 5 seconds

  implicit val mat = ActorMaterializer()

  implicit val transformer = MessageFlowTransformer.jsonMessageFlowTransformer[PlainMessage,PlainMessage]

  ClusterSharding(system).start(
    typeName = UserSocket.shardName,
    entityProps = UserSocket.props,
    settings = ClusterShardingSettings(system),
    extractShardId = UserSocket.extractShardId,
    extractEntityId = UserSocket.extractEntityId
  )

  val cluster = ClusterSharding(system).shardRegion(UserSocket.shardName)


  def postTosocket(postValue:String) = Action.async{
    val msg = ServiceResponse("heres/test",Json.obj("value" -> postValue),"user1")
    (cluster ? msg).mapTo[Either[String,String]] map {
      case Right(smsg) => Ok(smsg)
      case Left(emsg) => BadGateway(emsg)
    }
  }



  def index = Action {
    Ok(views.html.index())
  }

  override def sockjs: SockJS =
    SockJS.acceptOrResult[PlainMessage,PlainMessage] {
      request =>
        val user = "user1"
        cluster ! ConnectionRequest("user1")
        Future.successful(Right(ActorFlow.actorRef(out => WebSocket.props(user,out,cluster))))
    }

}

object Application