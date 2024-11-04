package com.example.datasetapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.datasetapp.data.repository.DatasetRepository
import com.example.datasetapp.data.source.network.model.DatasetResponse
import com.example.datasetapp.utils.Resource
import okhttp3.MultipartBody

class VerifikasiDataViewModel(private val repository : DatasetRepository) : ViewModel() {
    var nik: String? = null
    var name: String? = null

    fun uploadKtpKyc(image: MultipartBody.Part): LiveData<Resource<DatasetResponse>> {
        return repository.uploadImage(image)
    }
}