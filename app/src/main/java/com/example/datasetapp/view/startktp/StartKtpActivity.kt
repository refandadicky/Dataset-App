package com.example.datasetapp.view.startktp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.datasetapp.R
import com.example.datasetapp.databinding.ActivityStartKtpBinding
import com.example.datasetapp.view.cameraktp.CameraKtpFragment

class StartKtpActivity : AppCompatActivity() {
    private val binding: ActivityStartKtpBinding by lazy {
        ActivityStartKtpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnTakePictureKtp.setOnClickListener {
            val fragment = CameraKtpFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardActivity, fragment) // Pastikan R.id.fragment_container adalah ID dari container yang kamu gunakan untuk menampung fragment
                .addToBackStack(null) // Menambahkan fragment ke backstack agar bisa kembali
                .commit()
        }

    }
}