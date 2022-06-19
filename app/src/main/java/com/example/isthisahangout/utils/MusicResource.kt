package com.example.isthisahangout.utils

data class MusicResource<T>(
    val status: Status,
    val data: T?,
    val message: String?
) {

    companion object {
        fun <T> success(data: T?) = MusicResource(Status.SUCCESS, data, null)

        fun <T> error(message: String, data: T?) = MusicResource(Status.ERROR, data, message)

        fun <T> loading(data: T?) = MusicResource(Status.LOADING, data, null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}