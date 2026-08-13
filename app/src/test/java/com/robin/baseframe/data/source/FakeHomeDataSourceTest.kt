package com.robin.baseframe.data.source

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class FakeHomeDataSourceTest {

    @Test
    fun successScenarioReturnsConfiguredResponse() = runTest {
        val expected = HomeDataScenario.Success().response

        assertEquals(expected, FakeHomeDataSource(HomeDataScenario.Success()).getHomeData())
    }

    @Test
    fun emptyScenarioReturnsEmptyResponse() = runTest {
        assertEquals(
            HomeDataScenario.Success().response.copy(banner = emptyList(), channels = emptyList()),
            FakeHomeDataSource(HomeDataScenario.Empty).getHomeData()
        )
    }

    @Test
    fun errorScenarioThrowsConfiguredMessage() = runTest {
        val message = "Example data failed"

        try {
            FakeHomeDataSource(HomeDataScenario.Error(message)).getHomeData()
            fail("Expected the error scenario to fail")
        } catch (exception: IllegalStateException) {
            assertEquals(message, exception.message)
        }
    }
}
