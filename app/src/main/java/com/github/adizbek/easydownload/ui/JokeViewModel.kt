package com.github.adizbek.easydownload.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adizbek.easydownload.repository.JokeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JokeViewModel @Inject constructor(private val jokeRepository: JokeRepository) : ViewModel() {

    private val randomJokeRequest = jokeRepository.getRandom()

    val randomJokes = randomJokeRequest.asLiveData()

    val randomJokesLoading = randomJokeRequest.liveLoading()

    fun getJokes() = viewModelScope.launch {
        randomJokeRequest.call()
    }

    init {
        getJokes()
    }
}