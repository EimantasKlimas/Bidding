package bidding.endpoint

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import bidding.Config

import scala.concurrent.ExecutionContext

class Endpoint(routesProvider: RoutesProvider)(
  implicit
  actorSystem: ActorSystem,
  materializer: Materializer,
  executionContext: ExecutionContext
) {
  def start(config: Config.Http): Unit = {
    Http()
      .newServerAt(config.interface, config.port)
      .bind(routesProvider.main(config.pathPrefix))
  }
}

object Endpoint {
  def apply(routesProvider: RoutesProvider)(
    implicit
    actorSystem: ActorSystem,
    materializer: Materializer,
    executionContext: ExecutionContext
  ): Endpoint = new Endpoint(routesProvider)
}
