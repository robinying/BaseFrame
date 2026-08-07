package com.robin.baseframe.di

import android.content.Context
import com.robin.baseframe.app.network.interceptor.CacheInterceptor
import com.robin.baseframe.app.network.interceptor.logging.LogInterceptor
import com.robin.baseframe.app.network.MyInterceptor
import com.robin.baseframe.app.network.OkHttpEventListener
import com.robin.baseframe.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder().apply {
            eventListenerFactory(OkHttpEventListener.FACTORY)
            cache(Cache(File(context.cacheDir, "http_cache"), 10 * 1024 * 1024))
            addInterceptor(MyInterceptor())
            addInterceptor(CacheInterceptor())
            addInterceptor(LogInterceptor())
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")  // 占位，实际业务替换
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
