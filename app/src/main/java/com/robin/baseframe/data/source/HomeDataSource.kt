package com.robin.baseframe.data.source

import com.robin.baseframe.data.model.BannerDto
import com.robin.baseframe.data.model.ChannelDto
import com.robin.baseframe.data.model.HomeResponse

/**
 * Data source for the example home data.
 */
interface HomeDataSource {

    /**
     * Returns the next configured example response.
     */
    suspend fun getHomeData(): HomeResponse
}

/**
 * Deterministic outcomes supported by the example data source.
 */
sealed interface HomeDataScenario {

    /**
     * Returns a populated response for the normal example state.
     */
    data class Success(
        val response: HomeResponse = HomeResponse(
            banner = listOf(BannerDto(title = "Banner 1", imageUrl = "banner-1")),
            channels = listOf(ChannelDto(name = "推荐"), ChannelDto(name = "热门"))
        )
    ) : HomeDataScenario

    /**
     * Returns a successful response with no content.
     */
    data object Empty : HomeDataScenario

    /**
     * Fails the request with the supplied deterministic message.
     */
    data class Error(val message: String) : HomeDataScenario
}

/**
 * Local data source used by the example screen instead of a remote service.
 */
class FakeHomeDataSource(
    private val scenario: HomeDataScenario = HomeDataScenario.Success()
) : HomeDataSource {

    override suspend fun getHomeData(): HomeResponse = when (scenario) {
        is HomeDataScenario.Success -> scenario.response
        HomeDataScenario.Empty -> HomeResponse()
        is HomeDataScenario.Error -> throw IllegalStateException(scenario.message)
    }
}
