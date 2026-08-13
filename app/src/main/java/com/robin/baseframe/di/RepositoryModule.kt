package com.robin.baseframe.di

import com.robin.baseframe.data.repository.HomeRepositoryImpl
import com.robin.baseframe.data.source.FakeHomeDataSource
import com.robin.baseframe.data.source.HomeDataSource
import com.robin.baseframe.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 接口绑定模块 — Domain 接口 → Data 实现。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    companion object {

        @Provides
        @Singleton
        fun provideHomeDataSource(): HomeDataSource = FakeHomeDataSource()
    }
}
