package bidding.endpoint.routes

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.http.scaladsl.server.Route
import bidding.data.Model.BidResponse
import bidding.endpoint.routes.BiddingRoutes.BidRequest
import bidding.endpoint.routes.format.JsonFormat

import scala.concurrent.{ExecutionContext, Future}

//Used to generate custom status
trait NoContentDirective  {
 def handleNoContent(f: BidRequest => Future[Option[BidResponse]])(
   implicit um: FromRequestUnmarshaller[BidRequest],
   m: ToResponseMarshaller[BidResponse],
   ec: ExecutionContext
 ): Route = entity(as[BidRequest]){ a =>
   complete(f(a).map{ result =>
     val entity = result.map{ data =>
       HttpEntity.apply(
         ContentTypes.`application/json`,
         JsonFormat.
           bidResponseFormat
           .write(data)
           .toString()
       )
     }.getOrElse(HttpEntity.Empty)

     HttpResponse(
       status = if(result.isEmpty) StatusCodes.NoContent else StatusCodes.OK,
       entity = entity
     )
   })
 }
}
