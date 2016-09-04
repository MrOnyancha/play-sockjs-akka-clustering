package actors

import akka.actor.{Actor, Props}
import akka.stream.actor.ActorPublisher
import models.{PlainMessage, ServiceResponse}



/**
  * Created by prite on 03/09/2016.
  */

object KafkaOutGoingConnector {

  def props = Props[KafkaOutgoingConnector]



}

class KafkaOutgoingConnector extends Actor with ActorPublisher[ServiceResponse]{

  override def receive: Receive = {
    case pr:ServiceResponse =>
      onNext(pr)

  }
}
