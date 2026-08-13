package com.robin.baseframe.data.model

import com.robin.baseframe.domain.model.BannerData
import com.robin.baseframe.domain.model.ChannelData
import com.robin.baseframe.domain.model.HomeData

/**
 * Maps the transport response to the domain model used by the example feature.
 */
fun HomeResponse.toHomeData(): HomeData = HomeData(
    banner = banner.map { BannerData(title = it.title, imageUrl = it.imageUrl) },
    channels = channels.map { ChannelData(name = it.name) }
)
