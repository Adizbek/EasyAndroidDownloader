package com.github.adizbek.easydownload.library.http.resource

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class Resource<T>

class SuccessResource<T>(
    val data: T
) : Resource<T>()

class ErrorResource<T>(
    val data: T?
) : Resource<T>()

class LoadingResource<T> : Resource<T>()

abstract class NetworkResource<R> {

    private val data = MutableLiveData<Resource<R>>()

    private val isLoading = MutableLiveData<Boolean>()

    abstract suspend fun getApiRequest(): R

    @WorkerThread
    suspend fun call() {
        isLoading.postValue(true)
        data.postValue(LoadingResource())

        val response = getApiRequest()

        if (isSuccess(response)) {
            data.postValue(SuccessResource(response))
        } else {
            data.postValue(ErrorResource(response))
        }

        isLoading.postValue(false)
    }

    fun asLiveData() = data as LiveData<Resource<R>>

    fun liveLoading() = isLoading as LiveData<Boolean>

    protected fun isSuccess(response: R): Boolean {
        return response != null
    }
}