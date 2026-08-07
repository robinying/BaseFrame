package com.robin.baseframe.data.repository

import com.robin.baseframe.data.api.ApiService
import com.robin.baseframe.domain.model.BannerData
import com.robin.baseframe.domain.model.ChannelData
import com.robin.baseframe.domain.model.HomeData
import com.robin.baseframe.domain.repository.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HomeRepository 的 Data 层实现。
 *
 * 负责从远程 API 拉取数据并映射为 Domain Model。
 * 后续可在此叠加缓存策略（Cache DataSource + Remote DataSource）。
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : HomeRepository {

    override fun getHomeData(): Flow<HomeData> = flow {
        // TODO: 接入真实 API。当前为演示数据。
        delay(1000) // 模拟网络延迟
        emit(
            HomeData(
                banner = listOf(BannerData("Banner 1", "https://example.com/1.png")),
                channels = listOf(ChannelData("推荐"), ChannelData("热门"))
            )
        )
    }
}
