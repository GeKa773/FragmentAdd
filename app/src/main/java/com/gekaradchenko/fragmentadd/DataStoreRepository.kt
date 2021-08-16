package com.gekaradchenko.fragmentadd

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFERENCE_NAME = "preference_name"
private const val PREFERENCE_COUNT = "preference_count"
private const val COUNT = 1

class DataStoreRepository(context: Context) {
    private object PreferenceKeys {
        val count = preferencesKey<Int>(PREFERENCE_COUNT)
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME,
    )

    suspend fun saveCountToDataStore(count: Int?) {
        count?.let {
            dataStore.edit {
                it[PreferenceKeys.count] = count
            }
        }
    }

    val readDolorFromDataStore: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStoreRepository", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val cCount = it[PreferenceKeys.count] ?: COUNT
            cCount
        }
}