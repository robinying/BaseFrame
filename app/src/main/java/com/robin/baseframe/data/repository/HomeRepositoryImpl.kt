package com.robin.baseframe.data.repository

import com.robin.baseframe.data.source.HomeDataSource
import com.robin.baseframe.data.model.toHomeData
import com.robin.baseframe.domain.model.HomeData
import com.robin.baseframe.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HomeRepository 的 Data 层实现。
 *
 * 默认通过可配置的本地数据源提供确定性的示例数据，不在运行时依赖远程服务。
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
) : HomeRepository {

    override fun getHomeData(): Flow<HomeData> = flow {
        emit(homeDataSource.getHomeData().toHomeData())
    }
}
