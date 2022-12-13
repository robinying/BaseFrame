package com.robin.module_web

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileApiService {

    @GET
    @Streaming
    fun downloadFile(@Url url: String): ResponseBody
}