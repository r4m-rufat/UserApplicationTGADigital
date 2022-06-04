package com.codingwithrufat.userapplication.util

sealed class NetworkState<T: Any> {
    data class SUCCESS<T: Any>(val data: T): NetworkState<T>()
    data class ERROR<T: Any>(val exception: Throwable): NetworkState<T>()
    data class LOADING<T: Any>(val loading: Boolean): NetworkState<T>()
}
