package com.robin.baseframe.data.model

import com.robin.baseframe.domain.model.BannerData
import com.robin.baseframe.domain.model.ChannelData
import com.robin.baseframe.domain.model.HomeData
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeResponseMapperTest {

    @Test
    fun toHomeDataMapsTransportFieldsToDomainModel() {
        val response = HomeResponse(
            banner = listOf(BannerDto(title = "Banner", imageUrl = "image")),
            channels = listOf(ChannelDto(name = "推荐"))
        )

        assertEquals(
            HomeData(
                banner = listOf(BannerData(title = "Banner", imageUrl = "image")),
                channels = listOf(ChannelData(name = "推荐"))
            ),
            response.toHomeData()
        )
    }

    @Test
    fun toHomeDataMapsEmptyResponseToEmptyDomainModel() {
        assertEquals(HomeData(), HomeResponse().toHomeData())
    }
}
