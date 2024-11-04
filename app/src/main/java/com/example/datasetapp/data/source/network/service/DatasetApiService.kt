package com.example.datasetapp.data.source.network.service

import com.example.datasetapp.data.source.network.model.DatasetResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DatasetApiService {

    @Multipart
    @POST("secure-upload-multipart")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<DatasetResponse>
}