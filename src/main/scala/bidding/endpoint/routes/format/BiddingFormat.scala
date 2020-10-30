package bidding.endpoint.routes.format

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import bidding.data.Model.{Banner, BidResponse,  Device, Geo, Impression, Locale, Site, User}
import bidding.endpoint.routes.BiddingRoutes.BidRequest
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait BiddingFormat extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val localeFormat: RootJsonFormat[Locale] = new RootJsonFormat[Locale] {
    override def write(obj: Locale): JsValue = JsString(obj.toString.toUpperCase)

    override def read(json: JsValue): Locale = json match {
      case JsString(value) =>
        Locale.fromString(value)
      case _ =>
        throw DeserializationException("Unimplemented locale")
    }
  }

  implicit val geoFormat: RootJsonFormat[Geo] = jsonFormat1(Geo)
  implicit val deviceFormat: RootJsonFormat[Device] = jsonFormat2(Device)
  implicit val siteFormat: RootJsonFormat[Site] = jsonFormat2(Site)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val impressionFormat: RootJsonFormat[Impression] = jsonFormat8(Impression)
  implicit val bidRequest: RootJsonFormat[BidRequest] = jsonFormat5(BidRequest)

  implicit val bannerFormat: RootJsonFormat[Banner] = jsonFormat4(Banner)
  implicit val bidResponseFormat: RootJsonFormat[BidResponse] = jsonFormat5(BidResponse)
}

