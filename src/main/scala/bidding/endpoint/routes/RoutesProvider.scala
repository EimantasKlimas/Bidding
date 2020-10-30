package bidding.endpoint

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.AuthenticationResult
import akka.stream.Materializer
import bidding.Config.Service.Auth
import bidding.Routes

import scala.concurrent.{ExecutionContext, Future}

class RoutesProvider(
  authConfig: Auth,
  routes: Routes
){
    def main(
      routePrefix: String
    )(implicit executionContext: ExecutionContext, materializer: Materializer): Route = {
        pathPrefix(routePrefix) {
          extractRequest { implicit request =>
            decodeRequest {
              encodeResponse {
                optionalHeaderValueByName("X-Authorization") { authorizationBearerOption =>
                  optionalHeaderValueByName("X-Interaction") { interaction =>
                    optionalHeaderValueByName("X-Forwarded-For") { userIp =>
                       authenticateOrRejectWithChallenge(
                         authenticate(authorizationBearerOption)(_)
                       ) { authenticationResult =>
                         routes.biddingRoutes.routes()
                       }
                    }
                  }
                }
              }
            }
        }
    }
  }


    //Custom authentication, can be used to build all kinds of logic from here
      private def authenticate(authHeader: Option[String])(credentials: Option[HttpCredentials])
      (implicit executionContext: ExecutionContext, materializer: Materializer, request: HttpRequest): Future[AuthenticationResult[String]] = {
        val authenticationHeaderPattern = "Bearer (.+)".r

        authHeader match {
          case Some(authenticationHeaderPattern(apiKey)) =>
            Future.successful(AuthenticationResult.success("Custom Result"))
          case _ =>
            Future.failed(throw new Exception("Unable to Authenticate"))
    }
  }
}
