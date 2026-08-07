package com.robin.baseframe.domain.usecase

import com.robin.baseframe.base.arch.Result
import com.robin.baseframe.domain.model.HomeData
import com.robin.baseframe.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * 获取首页数据 UseCase。
 *
 * 封装业务逻辑：从 Repository 拉数据，统一包装为 Result<T>。
 * 无参数 UseCase 直接实现 invoke()，有参数时使用 Params data class。
 */
class GetHomeDataUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    operator fun invoke(): Flow<Result<HomeData>> = homeRepository.getHomeData()
        .map<HomeData, Result<HomeData>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { e -> emit(Result.Error(e, e.message)) }
}
