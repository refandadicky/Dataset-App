package com.example.datasetapp.data.repository

import com.example.datasetapp.data.source.network.model.DatasetResponse
import okhttp3.MultipartBody
import com.example.datasetapp.data.source.network.service.ApiConfig
import com.example.datasetapp.data.source.network.service.DatasetApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class DatasetRepository(
    private val contactAPI: DatasetApiService = ApiConfig.invoke()
) {
    suspend fun uploadImage(file: File) : Response<DatasetResponse> {
        val mediaType = "application/octet-stream".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, file)
        val multipartBodyPart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        return contactAPI.uploadImage(multipartBodyPart)

    }

}