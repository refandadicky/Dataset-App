package com.example.datasetapp.view.cameraselfie

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.datasetapp.R
import com.example.datasetapp.data.model.SelfiePhoto
import com.example.datasetapp.databinding.FragmentCameraSelfieBinding
import com.example.datasetapp.view.startselfie.SelfieViewModel
import com.example.datasetapp.view.verifikasiselfie.VerifikasiFotoSelfieFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class CameraSelfieFragment : Fragment() {

    private var _binding: FragmentCameraSelfieBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: SelfieViewModel by viewModel()

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imageCapture: ImageCapture? = null
    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraSelfieBinding.inflate(layoutInflater, container, false)

//        Log.d("Fragment", "Initial selfies size: ${viewModel.selfies.value?.size}")
        Log.d("Fragment", "Initial selfies size: ${viewModel.nik} ${viewModel.name}")
        mBinding.captureImage.setOnClickListener { takePhoto() }

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        startCamera()
        updateUIForCurrentPhoto()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build().also {
                    it.setSurfaceProvider(mBinding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val viewPort = ViewPort.Builder(Rational(350, 200), Surface.ROTATION_0).build()

            try {
                val useCaseGroup = UseCaseGroup.Builder()
                    .addUseCase(preview)
                    .addUseCase(imageCapture!!)
                    .setViewPort(viewPort)
                    .build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, useCaseGroup
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(), "Gagal memunculkan kamera.", Toast.LENGTH_SHORT
                ).show()
                Log.e("Camera", "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(), "Gagal memunculkan kamera.", Toast.LENGTH_SHORT
                ).show()
                Log.e("Camera", "stopCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createCustomTempFile(requireContext())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    currentImageUri = output.savedUri

                    encodeImage()

                    val currentSelfies = viewModel.selfies.value
                    if (currentSelfies != null && viewModel.currentPhotoIndex < currentSelfies.size) {
                        val currentSelfie = currentSelfies[viewModel.currentPhotoIndex]

                        // Buat instance SelfiePhoto baru dengan URI yang diperbarui
                        val updatedSelfie = SelfiePhoto(
                            id = currentSelfie.id,
                            imageUri = currentImageUri,
                            title = currentSelfie.title
                        )


                        // Log the URI before saving
                        Log.d("Camera", "Saving photo with URI: $currentImageUri")

                        viewModel.addSelfie(updatedSelfie)


                        viewModel.selfies.value?.let { selfies ->
                            val updatedPhoto = selfies.find { it.id == currentSelfie.id }
                            Log.d("Camera", "Verifikasi - URI selfie yang diperbarui: ${updatedPhoto?.imageUri}")
                        }

                        Toast.makeText(
                            requireContext(),
                            "Berhasil mengambil gambar.",
                            Toast.LENGTH_SHORT
                        ).show()


                        // Pindah ke foto berikutnya
                        if (viewModel.currentPhotoIndex < currentSelfies.size - 1) {
                            viewModel.currentPhotoIndex++
                            if (viewModel.currentPhotoIndex == 1){
                                viewModel.imageUri1 = currentImageUri
                            } else if (viewModel.currentPhotoIndex == 2) {
                                viewModel.imageUri2 = currentImageUri
                            } else if (viewModel.currentPhotoIndex == 3) {
                                viewModel.imageUri3 = currentImageUri
                            } else if (viewModel.currentPhotoIndex == 4) {
                                viewModel.imageUri4 = currentImageUri
                            } else if (viewModel.currentPhotoIndex == 5) {
                                viewModel.imageUri5 = currentImageUri
                            }
                            updateUIForCurrentPhoto() // Memperbarui UI untuk foto berikutnya
                        } else {
                            navigateToVerification()
                        }
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(), "Gagal mengambil gambar.", Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Camera", "onError: ${exc.message}")
                }
            })
    }

    private fun navigateToVerification () {
        // Semua foto telah diambil, lakukan tindakan selanjutnya
        val cameraSelfieFragment = VerifikasiFotoSelfieFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.StartSelfie, cameraSelfieFragment) // Ganti dengan ID container fragment Anda
            .addToBackStack(null)
            .commit()
        Toast.makeText(
            requireContext(),
            "Semua foto telah diambil!",
            Toast.LENGTH_SHORT
        ).show()
        stopCamera()
    }

    private fun createCustomTempFile(context: Context): File {
        val nik = viewModel.nik ?: "unknown"
        val nama = viewModel.name ?: "unknown"
        val atribut = mBinding.tvAtribut.text.toString()

        val filesDir = context.externalCacheDir
        return File.createTempFile(
            "KTP_${nik}_${nama}_${atribut}",
            ".jpg",
            filesDir
        )
    }

    private fun encodeImage() {
        currentImageUri?.let {
            try {
                val parcelFileDescriptor =
                    requireActivity().contentResolver.openFileDescriptor(it, "r")
                val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                val imageBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor?.close()
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
//                val bytes = byteArrayOutputStream.toByteArray()
//                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
//                globalViewModel.ktpImageBase64.value = base64Image
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUIForCurrentPhoto() {
        val currentSelfies = viewModel.selfies.value
        if (currentSelfies != null && viewModel.currentPhotoIndex < currentSelfies.size) {
            mBinding.tvAtribut.text = currentSelfies[viewModel.currentPhotoIndex].title
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCamera()
        _binding = null
    }
}
