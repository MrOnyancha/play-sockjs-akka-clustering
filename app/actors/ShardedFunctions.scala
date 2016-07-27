package actors


import actors.UserSocket.{ConnectionRequest, WebSocketInit}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import models.{PlainMessage, ServiceResponse}


/**
  * Created by pritesh on 14/07/16.
  */
trait ShardedFunctions {

  def shardName = "userSocket"

  val extractEntityId: ExtractEntityId = {
    case sr@ServiceResponse(path,body,key) => (key,sr)
    case req@ConnectionRequest(userId) => (userId,req)
    case ws@WebSocketInit(userId,_) => (userId,ws)

  }

  val extractShardId: ExtractShardId = {
    case ServiceResponse(path,body,key) => (key.hashCode % 2).toString
    case ConnectionRequest(userId) => (userId.hashCode % 2).toString
    case WebSocketInit(userId,_)   =>  (userId.hashCode % 2).toString
  }


}
