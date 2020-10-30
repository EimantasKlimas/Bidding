package bidding.service.actors

import akka.actor.{Actor, ActorSystem, Props, Scheduler}
import bidding.data.Model.{Device, Impression, Site, User}
import bidding.data.repositories.MockRepository
import bidding.service.actors.BiddingParentActor.CampaignBidRequest

import scala.concurrent.ExecutionContext

class BiddingParentActor(mockRepository: MockRepository)(
  implicit
  ec:ExecutionContext,
  sc: Scheduler,
  ac: ActorSystem
) extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case request: CampaignBidRequest =>
        val biddingActor= ac.actorOf(Props(new BiddingChildActor(mockRepository)))
        biddingActor forward request
  }
}

object BiddingParentActor {
  case class CampaignBidRequest(
    id: String,
    imp: Option[List[Impression]],
    site: Site,
    user: Option[User],
    device: Option[Device],
    useHardcoded: Boolean
  )
}
