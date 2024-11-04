package com.example.datasetapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.datasetapp.databinding.ActivityDashboardBinding
import com.example.datasetapp.view.CameraKtpFragment

class DashboardActivity : AppCompatActivity() {
    private val binding: ActivityDashboardBinding by lazy {
        ActivityDashboardBinding.inflate(layoutInflater)
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