package bidding.endpoint.routes.format

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import bidding.data.Model.{Banner, BidResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormat extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val bannerFormat: RootJsonFormat[Banner] = jsonFormat4(Banner)
  implicit val bidResponseFormat: RootJsonFormat[BidResponse] = jsonFormat5(BidResponse)
}
