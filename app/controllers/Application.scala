package controllers

import actors.UserSocket
import actors.UserSocket.WebSocketInit
import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import javax.inject.Inject
//import com.google.inject.Inject
import models.ServiceResponse
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext
//import play.sockjs.api.SockJS.MessageFlowTransformer

import scala.concurrent.duration._

class Application @Inject()(implicit system: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5 seconds

  implicit val mat = ActorMaterializer()

  //  implicit val transformer = MessageFlowTransformer.jsonMessageFlowTransformer[PlainMessage, PlainMessage]

  val certainActor: ActorRef = ClusterSharding(system).start(
    typeName = UserSocket.shardName,
    entityProps = UserSocket.props,
    settings = ClusterShardingSettings(system),
    extractShardId = UserSocket.extractShardId,
    extractEntityId = UserSocket.extractEntityId
  )

  //  val cluster = ClusterSharding(system).shardRegion(UserSocket.shardName)


  def postTosocket(postValue: String) = Action {
    val msg = ServiceResponse("heres/test", Json.obj("value" -> postValue), "user1")
    //    outgoing ! msg
    certainActor ! msg
    Ok("Sent")
  }


  implicit val ec: ExecutionContext = system.dispatcher
  system.scheduler.schedule(10.seconds, 1.second, certainActor, WebSocketInit("ONLINE", certainActor))

  //  def getMsg(): ServiceResponse =

  def index = Action {
    Ok(views.html.index())
  }


}

object Application