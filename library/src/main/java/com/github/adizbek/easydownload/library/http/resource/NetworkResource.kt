package com.github.adizbek.easydownload.library.http.resource

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class Resource<T>

class SuccessResource<T>(
    val data: T
) : Resource<T>()

class ErrorResource<T>(
    val err: Throwable,
    val data: T?
) : Resource<T>()

class LoadingResource<T> : Resource<T>()

class FailedNetworkRequest() : Error()

abstract class NetworkResource<R> {

    private val data = MutableLiveData<Resource<R>>()

    private val isLoading = MutableLiveData<Boolean>()

    abstract suspend fun getApiRequest(): R


    @Throws(Error::class)
    @WorkerThread
    suspend fun call(): R {
        isLoading.postValue(true)
        data.postValue(LoadingResource())

        var response: R

        try {
            response = getApiRequest()

            if (isSuccess(response)) {
                data.postValue(SuccessResource(response))
            } else {
                data.postValue(ErrorResource(FailedNetworkRequest(), response))
            }
        } catch (e: Throwable) {
            data.postValue(ErrorResource(e, null))

            throw e
        } finally {
            isLoading.postValue(false)
        }


        return response
    }

    fun asLiveData() = data as LiveData<Resource<R>>

    fun liveLoading() = isLoading as LiveData<Boolean>

    protected fun isSuccess(response: R): Boolean {
        return response != null
    }
}