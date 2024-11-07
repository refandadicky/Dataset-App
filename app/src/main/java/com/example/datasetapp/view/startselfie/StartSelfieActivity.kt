package com.example.datasetapp.view.startselfie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.datasetapp.R
import com.example.datasetapp.databinding.ActivityStartSelfieBinding
import com.example.datasetapp.view.cameraselfie.CameraSelfieFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartSelfieActivity : AppCompatActivity() {
    private val binding: ActivityStartSelfieBinding by lazy {
        ActivityStartSelfieBinding.inflate(layoutInflater)
    }

    private val viewModel: SelfieViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val nik: String? = intent.getStringExtra("nik")
        val nama: String? = intent.getStringExtra("nama")

        viewModel.nik = nik ?: ""
        viewModel.name = nama ?: ""

        Log.d("selfie cekk", "onCreate: ${viewModel.name}, ${viewModel.nik}")
        Log.d("selfie cekk2", "onCreate: ${nama}, ${nik}")

        binding.btnTakePictureSelfie.setOnClickListener {
            val fragment = CameraSelfieFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.StartSelfie, fragment) // Pastikan R.id.fragment_container adalah ID dari container yang kamu gunakan untuk menampung fragment
                .addToBackStack(null) // Menambahkan fragment ke backstack agar bisa kembali
                .commit()
        }
    }
}