package bidding

import akka.actor.{ActorSystem, Props, Scheduler}
import akka.routing.SmallestMailboxPool
import akka.stream.Materializer
import bidding.data.repositories.MockRepository
import bidding.endpoint.{Endpoint, RoutesProvider}
import bidding.service.actors.BiddingParentActor
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor

object Service extends App with LazyLogging {

    start(Config.build(ConfigFactory.load()))

    private def start(config: Config): Unit = {
      implicit val actorSystem: ActorSystem = ActorSystem("Service")
      implicit val materializer: Materializer = Materializer(actorSystem)
      implicit val executor: ExecutionContextExecutor = actorSystem.dispatcher
      implicit val scheduler: Scheduler = actorSystem.scheduler

      val mockRepository = new MockRepository()

      val providerActor = actorSystem.actorOf(SmallestMailboxPool(5).props(Props.apply[BiddingParentActor](new BiddingParentActor(mockRepository))))

      val routes = new Routes(providerActor)

      val routesProvider = new RoutesProvider(config.Service.Auth, routes)
      val endpoint = new Endpoint(routesProvider)

      endpoint.start(config.Http)
    }
}
