package com.robin.baseframe.ui.example

import com.robin.baseframe.MainDispatcherRule
import com.robin.baseframe.domain.model.ChannelData
import com.robin.baseframe.domain.model.HomeData
import com.robin.baseframe.domain.repository.HomeRepository
import com.robin.baseframe.domain.usecase.GetHomeDataUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun refreshCancelsPreviousCollectorBeforeStartingAnother() = runTest {
        val repository = CountingRepository()
        val viewModel = ExampleViewModel(GetHomeDataUseCase(repository))

        viewModel.onEvent(ExampleUiEvent.LoadHome)
        advanceUntilIdle()
        viewModel.onEvent(ExampleUiEvent.Refresh)
        advanceUntilIdle()

        assertEquals(2, repository.collectionCount)
        assertEquals(1, repository.cancelledCollectionCount)
    }

    @Test
    fun latestRefreshResultUpdatesState() = runTest {
        val first = HomeData(channels = listOf(ChannelData("first")))
        val second = HomeData(channels = listOf(ChannelData("second")))
        var requestCount = 0
        val repository = object : HomeRepository {
            override fun getHomeData(): Flow<HomeData> = flow {
                requestCount++
                emit(if (requestCount == 1) first else second)
            }
        }
        val viewModel = ExampleViewModel(GetHomeDataUseCase(repository))

        viewModel.onEvent(ExampleUiEvent.LoadHome)
        advanceUntilIdle()
        viewModel.onEvent(ExampleUiEvent.Refresh)
        advanceUntilIdle()

        assertEquals(second, viewModel.uiState.value.homeData)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    private class CountingRepository : HomeRepository {
        var collectionCount = 0
        var cancelledCollectionCount = 0

        override fun getHomeData(): Flow<HomeData> = flow {
            collectionCount++
            try {
                awaitCancellation()
            } finally {
                cancelledCollectionCount++
            }
        }
    }
}
