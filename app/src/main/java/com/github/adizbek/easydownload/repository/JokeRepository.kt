package com.github.adizbek.easydownload.repository

import com.github.adizbek.easydownload.api.JokeService
import com.github.adizbek.easydownload.api.data.Joke
import com.github.adizbek.easydownload.library.http.resource.NetworkResource
import javax.inject.Singleton

@Singleton
class JokeRepository(private val jokeService: JokeService) {

    fun getRandom(): NetworkResource<ArrayList<Joke>> {
        return object : NetworkResource<ArrayList<Joke>>() {
            override suspend fun getApiRequest(): ArrayList<Joke> {
                return jokeService.getRandomJokes()
            }
        }
    }

    fun nonExistsEndpoint(): NetworkResource<HashMap<String, String>> {
        return object: NetworkResource<HashMap<String, String>>() {
            override suspend fun getApiRequest(): HashMap<String, String> {
                return jokeService.notExistsGet()
            }
        }
    }
}