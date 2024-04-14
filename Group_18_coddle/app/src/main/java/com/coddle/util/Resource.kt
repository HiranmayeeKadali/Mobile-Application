package com.coddle.util

data class Resource<out T>(
    val state: ResourceState,
    val data: T? = null,
    val message: String? = null
) {
    sealed class ResourceState {
        object LOADING : ResourceState()
        object SUCCESS : ResourceState()
        object ERROR : ResourceState()
    }
}
