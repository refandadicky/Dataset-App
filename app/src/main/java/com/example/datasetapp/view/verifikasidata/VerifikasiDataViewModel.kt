package com.example.datasetapp.view.verifikasidata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datasetapp.data.repository.DatasetRepository
import kotlinx.coroutines.launch
import java.io.File

class VerifikasiDataViewModel(private val repository : DatasetRepository) : ViewModel() {
    var nik: String? = null
    var name: String? = null

    /*fun uploadKtpKyc(image: MultipartBody.Part): LiveData<Resource<DatasetResponse>> {
        return repository.uploadImage(image)
    }*/
    fun uploadKtpKyc(file: File, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Memanggil metode upload dari repository
                val response = repository.uploadImage(file)
                if (response.isSuccessful) {
                    onSuccess(response.body()?.toString() ?: "Upload successful")
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Exception: ${e.message}")
            }
        }
    }
}