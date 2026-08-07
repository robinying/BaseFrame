package com.robin.baseframe.domain.model

/**
 * 首页数据 — Domain Model，不依赖任何 DataSource。
 */
data class HomeData(
    val banner: List<BannerData> = emptyList(),
    val channels: List<ChannelData> = emptyList(),
    val lists: List<ListData> = emptyList()
)

data class BannerData(val title: String, val imageUrl: String)

data class ChannelData(val name: String)

data class ListData(val content: String)
