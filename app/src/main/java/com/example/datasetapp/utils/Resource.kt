package com.example.datasetapp.utils

data class Resource<T>(val status: StatusEnum, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(StatusEnum.SUCCESS, data, null)

        fun <T> error(msg: String?, data: T?): Resource<T> = Resource(StatusEnum.FAILED, data, msg)

        fun <T> loading(data: T?): Resource<T> = Resource(StatusEnum.LOADING, data, null)
    }
}