package com.example.datasetapp.view.finish

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.datasetapp.databinding.FragmentBerhasilUploadBinding
import com.example.datasetapp.databinding.FragmentCameraKtpBinding
import com.example.datasetapp.view.startktp.StartKtpActivity
import com.example.datasetapp.view.startselfie.SelfieViewModel
import com.example.datasetapp.view.verifikasidata.VerifikasiDataActivity
import org.koin.android.ext.android.inject

class BerhasilUploadFragment : Fragment() {

    private lateinit var mbinding: FragmentBerhasilUploadBinding
    private val viewModel: SelfieViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mbinding = FragmentBerhasilUploadBinding.inflate(layoutInflater, container, false)
        viewModel.nik = ""
        viewModel.name = ""
        viewModel.currentPhotoIndex = 0
        viewModel.resetSelfie()

        mbinding.btnTakePictureKtp.setOnClickListener {
            val intent = Intent(requireActivity(), StartKtpActivity::class.java)
            startActivity(intent)
        }

        mbinding.btnBack.setOnClickListener {
            val intent = Intent(requireActivity(), StartKtpActivity::class.java)
            startActivity(intent)
        }

        return mbinding.root
    }


}