package com.robin.baseframe.data.model

import com.google.gson.annotations.SerializedName

/**
 * 网络响应 DTO — 与服务端 JSON 结构对应。
 */
data class HomeResponse(
    @SerializedName("banner")
    val banner: List<BannerDto> = emptyList(),
    @SerializedName("channel")
    val channels: List<ChannelDto> = emptyList()
)

data class BannerDto(
    @SerializedName("title") val title: String = "",
    @SerializedName("imageUrl") val imageUrl: String = ""
)

data class ChannelDto(
    @SerializedName("name") val name: String = ""
)
