package com.example.datasetapp.view.verifikasiselfie

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datasetapp.R
import com.example.datasetapp.data.model.SelfiePhoto
import com.example.datasetapp.databinding.FragmentVerifikasiFotoSelfieBinding
import com.example.datasetapp.view.cameraselfie.CameraSelfieFragment
import com.example.datasetapp.view.finish.BerhasilUploadFragment
import com.example.datasetapp.view.startselfie.SelfieViewModel
import com.example.datasetapp.view.verifikasidata.VerifikasiDataViewModel
import com.example.datasetapp.view.verifikasiselfie.adapter.SelfiePhotoAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class VerifikasiFotoSelfieFragment : Fragment() {

    private var _binding: FragmentVerifikasiFotoSelfieBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SelfieViewModel by inject()

    private lateinit var recyclerView: RecyclerView
    private lateinit var selfiePhotoAdapter: SelfiePhotoAdapter
    lateinit var builder : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerifikasiFotoSelfieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(context).create()
        builder.setCancelable(false)
        // Inisialisasi RecyclerView
        recyclerView = binding.rvSelfie // Ganti dengan ID RecyclerView di layout Anda
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi Adapter
        selfiePhotoAdapter = SelfiePhotoAdapter(viewModel, mutableListOf()) { position ->
            navigateToCameraFragment(position) // Mengambil foto ulang
        }

        recyclerView.adapter = selfiePhotoAdapter

        // Observe changes in the selfies LiveData
        viewModel.selfies.observe(viewLifecycleOwner) { selfiePhotos ->
            selfiePhotoAdapter.updateSelfies(selfiePhotos) // Memperbarui adapter dengan selfie yang baru
            binding.btnTakePictureKtp.setOnClickListener {
                uploadAllSelfiePhotos(selfiePhotos)
            }
            Log.d("Fragment", "Selfies observed: ${selfiePhotos.size}")
        }
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun navigateToCameraFragment(position: Int) {
        val correction = true
        viewModel.currentPhotoIndex = position


        val cameraSelfieFragment = CameraSelfieFragment()
        val bundle = Bundle()
        bundle.putBoolean("correction", correction)
        cameraSelfieFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.StartSelfie, cameraSelfieFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Menghindari memory leaks
    }

    private fun uploadAllSelfiePhotos(selfiePhotos :  MutableList<SelfiePhoto>) {
        // Lakukan upload satu per satu
        uploadNextPhoto(selfiePhotos, 0)
    }

    private fun uploadNextPhoto(selfiePhotos:  MutableList<SelfiePhoto>, index: Int) {

        builder.setTitle("Uploading")
        builder.setMessage("${index + 1} / ${selfiePhotos.size}")

        if (index >= selfiePhotos.size) {
            // Semua foto sudah terupload
            builder.dismiss()
            Toast.makeText(requireContext(), "Semua foto selfie sudah terupload!", Toast.LENGTH_LONG).show()
            navigateToUploadBerhasilFragment()
            return
        }

        val photoUri = selfiePhotos[index].imageUri
        val file = photoUri?.toFile() ?: return
        val newFileName = "SELFIE_${viewModel.name}_${viewModel.nik}.${file.extension}" // Menambahkan ekstensi asli
        val newFile = File(file.parent, newFileName)
        if (file.renameTo(newFile)) {
            if (!builder.isShowing){
                builder.show()
            }
            viewModel.uploadSelfiePhoto(newFile,
                onSuccess = {
                    Log.d("UploadSuccess", "Photo ${index + 1} uploaded successfully.")
                    Log.d("UploadSuccess", "Response : ${it}")
                    uploadNextPhoto(selfiePhotos, index + 1) // Panggil fungsi ini untuk foto berikutnya
                },
                onError = { error ->
                    Log.e("UploadError", "Failed to upload photo ${index + 1}: $error")
                    Toast.makeText(requireContext(), "Upload gagal pada foto ${index + 1}: $error", Toast.LENGTH_LONG).show()
                    // Anda bisa memutuskan untuk menghentikan atau melanjutkan pada kesalahan
                    uploadNextPhoto(selfiePhotos, index + 1)
                }
            )
        } else {
            Log.e("FileRename", "Failed to rename file.")
        }
    }


    private fun navigateToUploadBerhasilFragment() {
        val berhasilFragment = BerhasilUploadFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.StartSelfie, berhasilFragment) // Pastikan ID ini sesuai dengan kontainer fragment di layout Anda
            .addToBackStack(null)
            .commit()
        Toast.makeText(requireContext(), "Semua foto selfie sudah terupload!", Toast.LENGTH_LONG).show()
    }

}
