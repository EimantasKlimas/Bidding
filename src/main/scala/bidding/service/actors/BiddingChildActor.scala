package bidding.service.actors

import akka.actor.{Actor, PoisonPill}
import bidding.Exceptions.LogicError
import bidding.data.Model.{Banner, BidResponse, Campaign, Device, Impression, User}
import bidding.data.repositories.MockRepository
import bidding.service.actors.BiddingParentActor.CampaignBidRequest

import scala.concurrent.ExecutionContext
import scala.util.Random

class BiddingChildActor(
  mockRepository: MockRepository
)(implicit ec:ExecutionContext) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case request: CampaignBidRequest =>
      val receiver = sender()
      for{
        campaigns <- mockRepository.mockCampaingData(request.useHardcoded)
      } yield {
        receiver ! findCampaign(
          request = request,
          campaigns = campaigns
        )
        self ! PoisonPill
      }
  }

  private def findCampaign(
    request: CampaignBidRequest,
    campaigns: Vector[Campaign]
  ): Option[BidResponse] = {
    val impressions = request
      .imp
      .getOrElse(Nil)

    val matchingCampaigns = campaigns
      .zip(impressions)
      .collect {
        case (campaign, impression) if
          impression.bidfloor.exists(_ <= campaign.bid) &&
            checkDimensions(
              banners = campaign.banners,
              impression = impression
            ) &&
            campaign.targeting.targetedSiteIds
              .contains(request.site.id) &&
            checkCountry(
              campaign = campaign,
              device = request.device,
              user = request.user
            )
        =>
          BidResponse(
            id = campaign.id.toString,
            bidRequestId = request.id,
            price = impression.bidfloor.getOrElse(throw LogicError),
            adid = Some(campaign.id.toString),
            banner = Random.shuffle(campaign.banners).headOption
          )
      }

    Random.shuffle(matchingCampaigns).headOption
  }


  private def checkCountry(
    campaign: Campaign,
    device: Option[Device],
    user: Option[User]
  ): Boolean = {
    def userCheck: Boolean = user.exists(_.geo.exists(_.country.contains(campaign.country)))

    device.map(_.geo.exists(_.country.contains(campaign.country))).getOrElse(userCheck)
  }

  private def checkDimensions(
    banners: List[Banner],
    impression: Impression
  ): Boolean = {
    (impression.h, impression.w) match {
      case (Some(height), Some(width)) =>
        banners.exists{ banner =>
          banner.height == height && banner.width == width
        }
      case (Some(height), None) =>
        banners.exists(_.height == height) &&
        checkAlternativeDimension(
          banners = banners,
          dimension = "WIDTH",
          minDimension = impression.wmin,
          maxDimension = impression.wmax
        )
      case (None, Some(width)) =>
        banners.exists(_.width == width) &&
          checkAlternativeDimension(
            banners = banners,
            dimension = "HEIGHT",
            minDimension = impression.hmin,
            maxDimension = impression.hmax
          )
      case (None, None) =>
        checkAlternativeDimensions(
          minHeight = impression.hmin,
          minWidth = impression.wmin,
          maxHeight = impression.hmax,
          maxWidth = impression.hmax,
          banners = banners
        )
    }
  }

  private def checkAlternativeDimensions(
    minHeight: Option[Int],
    minWidth: Option[Int],
    maxHeight: Option[Int],
    maxWidth: Option[Int],
    banners: List[Banner]
  ): Boolean = {
    val heightMatch: Boolean = checkAlternativeDimension(
      banners = banners,
      dimension = "HEIGHT",
      minDimension = minHeight,
      maxDimension = maxHeight
    )
    val widthMatch: Boolean = checkAlternativeDimension(
      banners = banners,
      dimension = "WIDTH",
      minDimension = minWidth,
      maxDimension = maxHeight
    )
      heightMatch && widthMatch
    }


  private def checkAlternativeDimension(
    banners: List[Banner],
    dimension: String,
    minDimension: Option[Int],
    maxDimension: Option[Int]
  ): Boolean = {
    def getBannerValue(banner: Banner) = dimension.toUpperCase() match {
      case "HEIGHT" =>
        banner.height
      case "WIDTH" =>
        banner.width
    }

    (minDimension, maxDimension) match {
      case (Some(min), Some(max)) =>
        filterBanners(
          banners,
          (banner: Banner) =>
            getBannerValue(banner) >= min && getBannerValue(banner) <= max
        ).nonEmpty
      case (Some(min), None) =>
        filterBanners(
          banners,
          (banner: Banner) =>
            getBannerValue(banner) >= min
        ).nonEmpty
      case (None, Some(max)) =>
        filterBanners(
          banners,
          (banner: Banner) =>
            getBannerValue(banner) <= max
        ).nonEmpty
      case _ =>
        false
    }

  }

  private def filterBanners(
    banners: List[Banner],
    filterParameters: Banner => Boolean
  ): List[Banner] = {
    banners.filter(filterParameters)
  }
}



