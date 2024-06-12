package com.catscoffeeandkitchen.exercises.youtube_player.di

import com.catscoffeeandkitchen.exercises.youtube_player.data.YouTubeEndpoints
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class YouTubeModule {

    @Provides
    @Singleton
    @Named("youtube_retrofit")
    fun provideYouTubeRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://youtube.googleapis.com")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideYouTubeEndpoints(
        @Named("youtube_retrofit") retrofit: Retrofit
    ): YouTubeEndpoints {
        return retrofit.create(YouTubeEndpoints::class.java)
    }
}