package com.catscoffeeandkitchen.openai_api.di

import android.content.Context
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.catscoffeeandkitchen.openai_api.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenAIModule {

    @Provides
    @Singleton
    fun provideOpenAIClient(): OpenAI {
        return OpenAI(
            OpenAIConfig(
                token = BuildConfig.OPEN_AI_API_KEY
            )
        )
    }
}