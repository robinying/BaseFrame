package com.robin.baseframe.data.api

import com.robin.baseframe.data.model.HomeResponse
import retrofit2.http.GET

/**
 * Retrofit API 接口 — 使用 suspend 函数，不依赖 CoroutineCallAdapterFactory。
 */
interface ApiService {

    @GET("home")
    suspend fun getHomeData(): HomeResponse
}
