package com.robin.baseframe.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val DEMO_PREFERENCES_NAME = "demo_preferences"
private const val RECENT_DEMO_SEPARATOR = ""
private const val MAX_RECENT_DEMOS = 10

private val Context.demoPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DEMO_PREFERENCES_NAME
)

/**
 * Persisted user preferences for catalog demos.
 *
 * The repository stores stable demo IDs only; localized titles and catalog item data remain owned
 * by the catalog layer. Recent IDs are ordered from most recently opened to least recently opened.
 */
@Singleton
class DemoPreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {

    @Inject
    constructor(@ApplicationContext context: Context) : this(context.demoPreferencesDataStore)

    /** Emits the persisted favorite and recently opened stable demo IDs. */
    val preferences: Flow<DemoPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { storedPreferences ->
            DemoPreferences(
                favoriteDemoIds = storedPreferences[FAVORITE_DEMO_IDS].orEmpty(),
                recentDemoIds = decodeRecentDemoIds(storedPreferences[RECENT_DEMO_IDS])
            )
        }

    /** Sets whether the stable [demoId] is a favorite. */
    suspend fun setFavorite(demoId: String, isFavorite: Boolean) {
        validateDemoId(demoId)
        dataStore.edit { storedPreferences ->
            val favoriteDemoIds = storedPreferences[FAVORITE_DEMO_IDS].orEmpty().toMutableSet()
            if (isFavorite) {
                favoriteDemoIds.add(demoId)
            } else {
                favoriteDemoIds.remove(demoId)
            }
            storedPreferences[FAVORITE_DEMO_IDS] = favoriteDemoIds
        }
    }

    /** Toggles whether the stable [demoId] is a favorite and returns its new state. */
    suspend fun toggleFavorite(demoId: String): Boolean {
        validateDemoId(demoId)
        var isFavorite = false
        dataStore.edit { storedPreferences ->
            val favoriteDemoIds = storedPreferences[FAVORITE_DEMO_IDS].orEmpty().toMutableSet()
            isFavorite = if (favoriteDemoIds.add(demoId)) {
                true
            } else {
                favoriteDemoIds.remove(demoId)
                false
            }
            storedPreferences[FAVORITE_DEMO_IDS] = favoriteDemoIds
        }
        return isFavorite
    }

    /** Records [demoId] as the most recently opened demo, retaining at most ten IDs. */
    suspend fun recordRecent(demoId: String) {
        validateDemoId(demoId)
        dataStore.edit { storedPreferences ->
            val recentDemoIds = decodeRecentDemoIds(storedPreferences[RECENT_DEMO_IDS])
                .filterNot { it == demoId }
                .toMutableList()
            recentDemoIds.add(0, demoId)
            storedPreferences[RECENT_DEMO_IDS] = encodeRecentDemoIds(recentDemoIds)
        }
    }

    private fun validateDemoId(demoId: String) {
        require(demoId.isNotBlank()) { "Demo ID must not be blank." }
        require(!demoId.contains(RECENT_DEMO_SEPARATOR)) {
            "Demo ID must not contain the reserved recent-demo separator."
        }
    }

    private companion object {
        val FAVORITE_DEMO_IDS = stringSetPreferencesKey("favorite_demo_ids")
        val RECENT_DEMO_IDS = stringPreferencesKey("recent_demo_ids")

        fun decodeRecentDemoIds(encodedDemoIds: String?): List<String> = encodedDemoIds
            ?.split(RECENT_DEMO_SEPARATOR)
            .orEmpty()
            .filter { it.isNotBlank() }
            .distinct()
            .take(MAX_RECENT_DEMOS)

        fun encodeRecentDemoIds(demoIds: List<String>): String = demoIds
            .distinct()
            .take(MAX_RECENT_DEMOS)
            .joinToString(RECENT_DEMO_SEPARATOR)
    }
}

/** Catalog preference values represented exclusively by stable demo IDs. */
data class DemoPreferences(
    val favoriteDemoIds: Set<String> = emptySet(),
    val recentDemoIds: List<String> = emptyList()
)
