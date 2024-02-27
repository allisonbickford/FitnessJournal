package com.catscoffeeandkitchen.data.di

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.catscoffeeandkitchen.data.PreferencesKeys

@InstallIn(SingletonComponent::class)
@Module
class SettingsDataModule {
    @Singleton
    @Provides
    fun provideDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            migrations = listOf(
                object : DataMigration<Preferences> {
                    override suspend fun cleanUp() {
                        // no-op
                        // this is typically used to fix external resources depending on preferences
                    }

                    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
                        return currentData.asMap().isEmpty()
                    }

                    override suspend fun migrate(currentData: Preferences): Preferences {
                        val prefs = currentData.toMutablePreferences()
                        prefs[stringPreferencesKey(PreferencesKeys.WEIGHT_UNIT)] = com.catscoffeeandkitchen.models.WeightUnit.Pounds.name
                        prefs[longPreferencesKey(PreferencesKeys.TIMER_1)] = 15L
                        prefs[longPreferencesKey(PreferencesKeys.TIMER_2)] = 30L
                        prefs[longPreferencesKey(PreferencesKeys.TIMER_3)] = 60L
                        prefs[longPreferencesKey(PreferencesKeys.TIMER_4)] = 90L
                        prefs[longPreferencesKey(PreferencesKeys.TIMER_5)] = 120L

                        return prefs
                    }

                }
            ),
            produceFile = {
                context.preferencesDataStoreFile("LiftingLogPreferences")
            }
        )
    }
}
