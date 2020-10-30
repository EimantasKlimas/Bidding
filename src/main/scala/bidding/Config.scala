package bidding

import bidding.Config.Service.Auth

object Config {

    case class Service(
    Auth: Auth
  )

  object Service {
    def apply(config: com.typesafe.config.Config) = new Service(
      Auth = Auth(config.getConfig("auth"))
    )
    
    case class Auth(
      apiKey: String
    )

    object Auth {
      def apply(config: com.typesafe.config.Config): Auth =
        new Auth(
          apiKey = config.getString("apiKey")
        )
    }
  }

    case class Http(
    interface: String,
    pathPrefix: String,
    port: Int
  )

  object Http {
    def apply(config: com.typesafe.config.Config) = new Http(
      interface = config.getString("interface"),
      pathPrefix = config.getString("pathPrefix"),
      port = config.getInt("port")
    )
  }

    def build(config: com.typesafe.config.Config): Config = {
    Config(
      Service = Config.Service(config.getConfig("service")),
      Http = Config.Http(config.getConfig("http"))
    )
  }
}

case class Config(
  Service: Config.Service,
  Http: Config.Http
)


