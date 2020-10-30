package bidding

import akka.actor.{ActorRef, ActorSystem, Scheduler}

import scala.concurrent.ExecutionContext
import bidding.endpoint.routes.BiddingRoutes

class Routes(providerActor: ActorRef)(implicit ec: ExecutionContext, sc: Scheduler, as: ActorSystem) {

    val biddingRoutes = new BiddingRoutes(providerActor)
}

object Routes{
    def apply(providerActor: ActorRef)(implicit ec: ExecutionContext, sc: Scheduler, as: ActorSystem) = new Routes(providerActor)
}