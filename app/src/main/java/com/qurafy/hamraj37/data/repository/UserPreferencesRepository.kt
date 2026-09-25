package com.qurafy.hamraj37.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qurafy.hamraj37.data.model.PdfDrawingStroke
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class NightModePreference {
    SYSTEM,
    DARK,
    LIGHT
}

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val LAST_READ_PAGE = intPreferencesKey("last_read_page")
        val BOOKMARKED_PAGES = stringSetPreferencesKey("bookmarked_pages")
        val NIGHT_MODE = stringPreferencesKey("night_mode")
        val SELECTED_RECITER_ID = stringPreferencesKey("selected_reciter_id")
        val TEXT_SIZE_SP = floatPreferencesKey("text_size_sp")
        val PAGE_ANNOTATIONS_JSON = stringPreferencesKey("page_annotations_json")
    }

    val lastReadPage: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_READ_PAGE] ?: 1
    }

    suspend fun setLastReadPage(page: Int) {
        val validPage = page.coerceAtLeast(1)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_READ_PAGE] = validPage
        }
    }

    val bookmarkedPages: Flow<Set<Int>> = context.dataStore.data.map { preferences ->
        val stringSet = preferences[PreferencesKeys.BOOKMARKED_PAGES] ?: emptySet()
        stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    suspend fun removeBookmark(page: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.BOOKMARKED_PAGES] ?: emptySet()
            preferences[PreferencesKeys.BOOKMARKED_PAGES] = current - page.toString()
        }
    }

    suspend fun toggleBookmark(page: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.BOOKMARKED_PAGES] ?: emptySet()
            val strPage = page.toString()
            if (current.contains(strPage)) {
                preferences[PreferencesKeys.BOOKMARKED_PAGES] = current - strPage
            } else {
                preferences[PreferencesKeys.BOOKMARKED_PAGES] = current + strPage
            }
        }
    }

    val selectedReciterId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_RECITER_ID] ?: "alafasy"
    }

    suspend fun setSelectedReciterId(reciterId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_RECITER_ID] = reciterId
        }
    }

    val nightModePreference: Flow<NightModePreference> = context.dataStore.data.map { preferences ->
        val raw = preferences[PreferencesKeys.NIGHT_MODE] ?: NightModePreference.SYSTEM.name
        try {
            NightModePreference.valueOf(raw)
        } catch (_: Exception) {
            NightModePreference.SYSTEM
        }
    }

    suspend fun setNightModePreference(mode: NightModePreference) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NIGHT_MODE] = mode.name
        }
    }

    val textSizeSp: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TEXT_SIZE_SP] ?: 16f
    }

    suspend fun setTextSizeSp(size: Float) {
        val validSize = size.coerceIn(12f, 32f)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEXT_SIZE_SP] = validSize
        }
    }

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val annotationMapType = Types.newParameterizedType(
        Map::class.java,
        Int::class.javaObjectType,
        Types.newParameterizedType(List::class.java, PdfDrawingStroke::class.java)
    )
    private val annotationAdapter = moshi.adapter<Map<Int, List<PdfDrawingStroke>>>(annotationMapType)

    val pageAnnotations: Flow<Map<Int, List<PdfDrawingStroke>>> = context.dataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.PAGE_ANNOTATIONS_JSON] ?: ""
        if (rawJson.isBlank()) {
            emptyMap()
        } else {
            try {
                annotationAdapter.fromJson(rawJson) ?: emptyMap()
            } catch (_: Exception) {
                emptyMap()
            }
        }
    }

    suspend fun savePageAnnotations(annotations: Map<Int, List<PdfDrawingStroke>>) {
        try {
            val jsonString = annotationAdapter.toJson(annotations)
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.PAGE_ANNOTATIONS_JSON] = jsonString
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
