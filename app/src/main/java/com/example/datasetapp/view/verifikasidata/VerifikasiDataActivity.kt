package com.example.datasetapp.view.verifikasidata

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.example.datasetapp.databinding.ActivityVerifikasiDataBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.datasetapp.view.startselfie.StartSelfieActivity
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

        if (imageUri != null) {
            val imageUriParsed = Uri.parse(imageUri.toString())
            binding.ivKtp.setImageURI(imageUriParsed)
        }

        binding.btnLanjut.setOnClickListener {
            imageUri?.let { uri ->
                val nik = binding.etInputNik.text.toString()
                val nama = binding.etInputName.text.toString()
                uploadkatp(uri, nik, nama)
            }
        }

    }

    private fun uploadkatp(imageUri: Uri, nik: String, nama : String){

            val file = imageUri.toFile()
            val newFileName = "KTP_${nama}_$nik.${file.extension}" // Menambahkan ekstensi asli
            val newFile = File(file.parent, newFileName) // Lokasi dan nama file baru

            // Mengganti nama file
            if (file.renameTo(newFile)) {
                Log.d("FileRename", "File renamed to: ${newFile.name}")

                // Memanggil ViewModel untuk meng-upload gambar
                viewModel.uploadKtpKyc(newFile,
                    onSuccess = { response ->
                        Log.d("UploadSuccess", "Response: $response")
                        val intent = Intent(this, StartSelfieActivity::class.java)
                        intent.putExtra("nik", nik)
                        intent.putExtra("nama", nama)
                        startActivity(intent)
                        /*val fragment = CameraSelfieFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.blank_page, fragment)
                            .addToBackStack(null)
                            .commit()*/
                    },
                    onError = { error ->
                        Log.e("UploadError", "Error: $error")
                        Toast.makeText(this, "Upload gagal: $error", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                Log.e("FileRename", "Failed to rename file.")
                // Tangani kegagalan mengganti nama file (misalnya, tampilkan pesan kesalahan)
            }

            Log.d("Cekkk", "uploadkatp: Selesai upload")

    }
}
