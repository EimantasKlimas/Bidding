package bidding.data

import bidding.data.Model.Language.{Bengali, English, Hindu, Lithuanian, Thai}
import com.typesafe.scalalogging.LazyLogging

import scala.util.Random

object Model extends LazyLogging {
    //Traits
    trait Language
    trait Locale  {
        def getLanguage: Language
    }
    trait Country extends Locale
    //Objects/caseObjects

    object Locale {
        case object Lithuania extends Country {
            override def getLanguage: Language = Lithuanian
            override def toString: String = "LT"
        }
        case object Thailand extends Country {
            override def getLanguage: Language = Thai
            override def toString: String = "TH"
        }
        case object India extends Country {
            override def getLanguage: Language = Hindu
            override def toString: String = "IN"
        }
        case object Nigeria extends Country {
            override def getLanguage: Language = English
            override def toString = "NG"
        }
        case object Bangladesh extends Country {
            override def getLanguage: Language = Bengali
            override def toString = "BD"
        }
        case object Uninplemented extends Locale {
            override def getLanguage: Language = English
        }

        def fromString(value: String): Locale = value match {
            case "LT" =>
                Lithuania
            case "TH" =>
                Thailand
            case "IN" =>
                India
            case "NG" =>
                Nigeria
            case "BD" =>
                Bangladesh
            case unimplemented @ _ =>
                logger.info(s"Received unimplemented value: $unimplemented")
                Uninplemented
        }

        //used for mock generation
        def getRandom: Locale = {
            Random.nextInt(5) match {
                case 0 =>
                    Lithuania
                case 1 =>
                    Thailand
                case 2 =>
                    India
                case 3 =>
                    Nigeria
                case 4 =>
                    Bangladesh
                case _ =>
                    Uninplemented
            }
        }
    }

    object Language {
        case object Lithuanian extends Language
        case object Thai extends Language
        case object Hindu extends Language
        case object English extends Language
        case object Bengali extends Language
    }
    //Case class'es
    case class Campaign(
      id: Int,
      country: Locale,
      targeting: Targeting,
      banners: List[Banner],
      bid: Double
    )
    
    case class Targeting(
        // Vector chosen due to data presented in: https://docs.scala-lang.org/overviews/collections/performance-characteristics.html
      targetedSiteIds: Vector[String] = Vector.fill(Random.nextInt(5))(Random.nextString(10))
    )
    
    case class
    Banner(
      id: Int,
      src: String,
      width: Int,
      height: Int
    )

    case class Impression(
      id: String,
      wmin: Option[Int],
      wmax: Option[Int],
      w: Option[Int],
      hmin: Option[Int],
      hmax: Option[Int],
      h: Option[Int],
      //camel case error present due to being present in example request body
      bidfloor: Option[Double]
    )

    case class Site(
      id: String,
      domain: String
    )

    case class User(
      id: String,
      geo: Option[Geo]
    )
    
    case class Device(
      id: String,
      geo: Option[Geo]
    )

    case class Geo(
      country: Option[Locale]
    )

    case class BidResponse(
      id: String,
      bidRequestId: String,
      price: Double,
      adid: Option[String],
      banner: Option[Banner]
    )
}
