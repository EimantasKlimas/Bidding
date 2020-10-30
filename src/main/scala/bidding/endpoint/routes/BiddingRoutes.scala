package bidding.endpoint.routes

import akka.actor._
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import bidding.data.Model._
import bidding.endpoint.routes.BiddingRoutes.BidRequest
import bidding.endpoint.routes.format.BiddingFormat
import bidding.service.actors.BiddingParentActor.CampaignBidRequest

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class BiddingRoutes(biddingActor: ActorRef)(
  implicit ec: ExecutionContext, sc: Scheduler, as: ActorSystem)
  extends LazyLogging
    with BiddingFormat
    with NoContentDirective {
    implicit val timeout: Timeout = Timeout(100.milliseconds)
    def routes()(implicit ec: ExecutionContext): Route = {
        pathPrefix("bid") {
          path("regular") {
            handleNoContent { request: BidRequest =>
              processRequest(request)
            }
          } ~ path("hardcoded") {
            handleNoContent { request: BidRequest =>
              processRequest(request, true)
            }
          }
        }
    }

    def processRequest(request: BidRequest, useHardcoded: Boolean = false): Future[Option[BidResponse]] = {
        for {
            result <- (
              biddingActor ? CampaignBidRequest(
                  id = request.id,
                  imp = request.imp,
                  site = request.site,
                  user = request.user,
                  device = request.device,
                  useHardcoded = useHardcoded
              )
              ).mapTo[Option[BidResponse]]
        } yield {
            result
        }
    }
}

object BiddingRoutes {
    case class BidRequest(
      id: String,
      imp: Option[List[Impression]],
      site: Site,
      user: Option[User],
      device: Option[Device]
    )
}