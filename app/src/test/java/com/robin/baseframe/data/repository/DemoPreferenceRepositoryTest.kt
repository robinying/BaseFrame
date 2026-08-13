package com.robin.baseframe.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DemoPreferenceRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun favoritesCanBeAddedToggledAndRemoved() = runTest {
        val repository = createRepository()

        repository.setFavorite("flow", true)
        assertTrue(repository.preferences.first().favoriteDemoIds.contains("flow"))

        assertFalse(repository.toggleFavorite("flow"))
        assertFalse(repository.preferences.first().favoriteDemoIds.contains("flow"))

        assertTrue(repository.toggleFavorite("camera"))
        repository.setFavorite("camera", false)
        assertTrue(repository.preferences.first().favoriteDemoIds.isEmpty())
    }

    @Test
    fun recentDemosAreUniqueAndMostRecentFirst() = runTest {
        val repository = createRepository()

        repository.recordRecent("flow")
        repository.recordRecent("camera")
        repository.recordRecent("flow")

        assertEquals(listOf("flow", "camera"), repository.preferences.first().recentDemoIds)
    }

    @Test
    fun recentDemosAreLimitedToTenEntries() = runTest {
        val repository = createRepository()
        val demoIds = (1..11).map { "demo_$it" }

        demoIds.forEach { demoId -> repository.recordRecent(demoId) }

        assertEquals(demoIds.reversed().take(10), repository.preferences.first().recentDemoIds)
    }

    private fun createRepository(): DemoPreferenceRepository {
        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = { temporaryFolder.newFile("demo_preferences.preferences_pb") }
        )
        return DemoPreferenceRepository(dataStore)
    }
}
