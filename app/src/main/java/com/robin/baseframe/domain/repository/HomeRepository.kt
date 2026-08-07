package com.robin.baseframe.domain.repository

import com.robin.baseframe.domain.model.HomeData
import kotlinx.coroutines.flow.Flow

/**
 * 首页 Repository 接口 — 定义在 Domain 层，实现在 Data 层。
 * ViewModel 只依赖此接口，不感知数据来源（网络/缓存）。
 */
interface HomeRepository {

    /**
     * 获取首页数据。返回 Flow 以便观察缓存 + 网络多数据源。
     */
    fun getHomeData(): Flow<HomeData>
}
