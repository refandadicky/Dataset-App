package com.example.datasetapp.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.example.datasetapp.R
import com.example.datasetapp.databinding.ActivityVerifikasiDataBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.datasetapp.utils.Resource
import com.example.datasetapp.utils.StatusEnum
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class VerifikasiDataActivity : AppCompatActivity() {
    private val binding: ActivityVerifikasiDataBinding by lazy {
        ActivityVerifikasiDataBinding.inflate(layoutInflater)
    }
    private val viewModel: VerifikasiDataViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageUri: Uri? = intent.getParcelableExtra("image_uri")
        val photoPath = intent.getStringExtra("photo_path") ?: ""

        // Menampilkan gambar jika ada
        if (imageUri != null) {
            val imageUriParsed = Uri.parse(imageUri.toString())
            binding.ivKtp.setImageURI(imageUriParsed)
        }

        // Menyimpan input NIK dan Nama ke dalam ViewModel
        val nik = binding.etInputNik.text.toString()
        val nama = binding.etInputName.text.toString()

        viewModel.nik = nik
        viewModel.name = nama

        binding.btnLanjut.setOnClickListener(uploadkatp(imageUri!!, nik, nama))

    }

    private fun uploadkatp(imageUri: Uri, nik: String, nama : String): View.OnClickListener {
        return View.OnClickListener {
            val file = imageUri.toFile()
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = requestFile.let {
                MultipartBody.Part.createFormData(
                    "image",
                    "KTP_${nik}_${nama}",
                    it
                )
            }

            // Log untuk cek apakah uploadKtpKyc dipanggil
            Log.d("UploadStatus", "uploadkatp: Mulai upload")

            viewModel.uploadKtpKyc(body).observe(this) { resource ->
                when (resource.status) {
                    StatusEnum.SUCCESS -> {
                        // Log response jika sukses
                        Log.d("UploadSuccess", "Response: ${resource.data}")
                    }

                    StatusEnum.FAILED -> {
                        // Log error jika gagal
                        Log.e("UploadError", "Error: ${resource.message}")
                    }

                    StatusEnum.LOADING -> {
                        // Log atau tampilkan loading jika diperlukan
                        Log.d("UploadLoading", "Uploading...")
                    }
                }
            }

            // Ganti fragment setelah upload
            val fragment = CameraSelfieFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.blank_page, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun renameFile(oldPath: String, newFile: File): String {
        val oldFile = File(oldPath)

        return if (oldFile.exists() && oldFile.renameTo(newFile)) {
            newFile.absolutePath
        } else {
            oldPath
        }
    }
}
