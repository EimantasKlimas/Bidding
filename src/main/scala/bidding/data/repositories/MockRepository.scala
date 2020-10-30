package bidding.data.repositories

import bidding.data.Model.{Banner, Campaign, Locale, Targeting}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

//Tech. description does not designate what database or access library to use, so a mock repository is chosen,
// personal preference would be Slick or scalalikejdbc based on complexity of the project
class MockRepository(implicit ec: ExecutionContext) {

  private final val HARDCODED_CAMPAIGN: Campaign = Campaign(
    id = 1,
    country = Locale.Lithuania,
    targeting = Targeting(
      targetedSiteIds = Vector("0006a522ce0f4bbbbaa6b3c38cafaa0f") // Use collection of your choice
    ),
    banners = List(
      Banner(
        id = 1,
        src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
        width = 300,
        height = 250
      )
    ),
    bid = 5d
  )
  private final val DEFAULT_COST_CEILING: Int = 10
  private final val DEFAULT_MOCK_DATA_LENGHT: Int = 50
  private final val DEFAULT_DIMENSION_CEILING: Int = 400

  def mockCampaingData(
    useHardcoded: Boolean,
    lenght: Int = DEFAULT_MOCK_DATA_LENGHT
   ): Future[Vector[Campaign]] = {
    if(useHardcoded) Future(Vector(HARDCODED_CAMPAIGN)) else
    Future(Vector.fill(lenght)(generateMockData))
  }

  private def generateMockData: Campaign = {
    Campaign(
      id = Random.nextInt(),
      country = Locale.getRandom,
      targeting = Targeting(),
      banners = generateMockBanners,
      bid = Random.nextInt(DEFAULT_COST_CEILING)
    )
  }

  private def generateMockBanners: List[Banner] = {
    List.fill(Random.nextInt(DEFAULT_MOCK_DATA_LENGHT))(
      Banner(
        id = Random.nextInt(),
        src = Random.nextString(DEFAULT_MOCK_DATA_LENGHT),
        width = Random.nextInt(DEFAULT_DIMENSION_CEILING),
        height = Random.nextInt(DEFAULT_DIMENSION_CEILING)
      )
    )
  }
}



