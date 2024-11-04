package com.example.datasetapp.data.repository

import android.util.Log
import com.example.datasetapp.data.source.network.model.DatasetResponse
import okhttp3.MultipartBody
import androidx.lifecycle.liveData
import com.example.datasetapp.data.source.network.service.ApiConfig
import com.example.datasetapp.data.source.network.service.DatasetApiService
import com.example.datasetapp.utils.Resource
import org.json.JSONObject

class DatasetRepository(
    private val contactAPI: DatasetApiService = ApiConfig.invoke()
) {
    fun uploadImage(url: MultipartBody.Part) =
        liveData<Resource<DatasetResponse>> {
            emit(Resource.loading(null))
            try {
                val response = contactAPI.uploadImage(url)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Resource.success(it))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UploadError", "Response error: $errorBody")
                    val jsonObj = JSONObject(errorBody ?: "{}")
                    emit(Resource.error(jsonObj.getString("message"), null))
                }
            } catch (e: Exception) {
                Log.e("UploadError", "Exception: ${e.message}")
                emit(Resource.error(e.message, null))
            }
        }

}