package controllers

import actors.{KafkaIncomingConnector, KafkaOutGoingConnector, UserSocket, WebSocket}
import actors.UserSocket.ConnectionRequest
import akka.Done
import akka.actor.{ActorNotFound, ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.sockjs.api.{SockJS, SockJSRouter}
import akka.pattern.{AskTimeoutException, ask}
import akka.stream.actor.ActorPublisher
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import models.{PlainMessage, ServiceResponse}
import org.apache.kafka.clients.ClientRequest
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import play.api.Logger
import play.api.libs.streams.ActorFlow
import play.sockjs.api.SockJS.MessageFlowTransformer
import util.{JsonDeserializer, JsonSerializer}

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


  //outgoing
  val prodSettings = ProducerSettings(system,new StringSerializer, new JsonSerializer[ServiceResponse])
    .withBootstrapServers("localhost:9092")

  val outgoing = system.actorOf(KafkaOutGoingConnector.props)
  val pub = ActorPublisher[ServiceResponse](outgoing)

  Source.fromPublisher(pub)
    .map{ elem => new ProducerRecord[String,ServiceResponse]("chat",elem)}
    .runWith(Producer.plainSink(prodSettings))



  //incoming
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new JsonDeserializer[ServiceResponse])
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  Consumer.atMostOnceSource(consumerSettings, Subscriptions.topics("chat")).mapAsync(1){
    rec =>
      cluster ! rec.value()
      Logger.debug("Consumed")
      Future.successful(Done)
  }.runWith(Sink.ignore)


  def postTosocket(postValue:String) = Action{
    val msg = ServiceResponse("heres/test",Json.obj("value" -> postValue),"user1")
    outgoing ! msg
    Ok("Sent")
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