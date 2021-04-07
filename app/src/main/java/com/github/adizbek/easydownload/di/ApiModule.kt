package com.github.adizbek.easydownload.di

import com.github.adizbek.easydownload.api.JokeService
import com.github.adizbek.easydownload.repository.JokeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun getOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                val logger = HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }

                addInterceptor(logger)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideJokeService(client: OkHttpClient): JokeService {
        return Retrofit.Builder()
            .baseUrl("https://official-joke-api.appspot.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JokeService::class.java)
    }

    @Provides
    @Singleton
    fun provideJokeRepository(jokeService: JokeService): JokeRepository {
        return JokeRepository(jokeService)
    }
}