package com.catscoffeeandkitchen.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.catscoffeeandkitchen.models.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val preferences: DataStore<Preferences>
) {
    suspend fun setWeightUnit(unit: WeightUnit) {
        preferences.edit { prefs ->
            prefs[stringPreferencesKey(PreferencesKeys.WEIGHT_UNIT)] = unit.name
        }
    }

    fun getWeightUnit(): Flow<WeightUnit> = preferences.data.map { prefs ->
        WeightUnit.valueOf(
            prefs[stringPreferencesKey(PreferencesKeys.WEIGHT_UNIT)] ?: WeightUnit.Pounds.name)
    }

    suspend fun setTimer(index: Int, seconds: Long) {
        preferences.edit { prefs ->
            prefs[longPreferencesKey("timer${index.coerceIn(1, 5)}")] = seconds
        }
    }

    fun getTimers(): Flow<List<Long>> = combine(listOf(
        preferences.data.map { it[longPreferencesKey(PreferencesKeys.TIMER_1)] ?: 15L },
        preferences.data.map { it[longPreferencesKey(PreferencesKeys.TIMER_2)] ?: 30L },
        preferences.data.map { it[longPreferencesKey(PreferencesKeys.TIMER_3)] ?: 60L },
        preferences.data.map { it[longPreferencesKey(PreferencesKeys.TIMER_4)] ?: 90L },
        preferences.data.map { it[longPreferencesKey(PreferencesKeys.TIMER_5)] ?: 120L }
    )) { it.toList() }
}
