package com.example.datasetapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.datasetapp.databinding.ActivityMainBinding
import com.example.datasetapp.view.startktp.StartKtpActivity

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = Intent(this, StartKtpActivity::class.java)
        startActivity(intent)
        finish()
    }
}