package actors

import akka.actor.{Actor, Props}
import akka.stream.actor.ActorPublisher
import models.ServiceResponse


/**
  * Created by prite on 03/09/2016.
  */

object KafkaIncomingConnector {

  def props =Props[KafkaIncomingConnector]



}


class KafkaIncomingConnector extends Actor with ActorPublisher[ServiceResponse]{

  override def receive: Receive = {
    case sr:ServiceResponse =>
      onNext(sr)
  }
}
