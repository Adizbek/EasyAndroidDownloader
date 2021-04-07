package com.github.adizbek.easydownload.api

import com.github.adizbek.easydownload.api.data.Joke
import retrofit2.http.GET

interface JokeService {

    @GET("random_ten")
    suspend fun getRandomJokes(): ArrayList<Joke>

}