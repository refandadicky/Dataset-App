package com.example.datasetapp.view.cameraktp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.datasetapp.databinding.FragmentCameraKtpBinding
import com.example.datasetapp.view.verifikasidata.VerifikasiDataActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class CameraKtpFragment : Fragment() {

    private lateinit var mBinding: FragmentCameraKtpBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var currentImageUri: Uri? = null
    private lateinit var currentPhotoPath: String

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCameraKtpBinding.inflate(layoutInflater, container, false)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        mBinding.captureImage.setOnClickListener { takePhoto() }

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        startCamera()
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

            val viewPort = ViewPort.Builder(Rational(mBinding.previewView.width, mBinding.previewView.height), Surface.ROTATION_0).build()

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
        val photoFile = createCustomTempFile(requireActivity())
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile)
                .build()
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    currentImageUri = output.savedUri

                    encodeImage()
                    mBinding.previewView
                    mBinding.captureImage
                    Toast.makeText(
                        requireContext(), "Berhasil mengambil gambar.", Toast.LENGTH_SHORT
                    ).show()
                    stopCamera()

                    if (currentImageUri != null) {
                        val intent = Intent(requireActivity(), VerifikasiDataActivity::class.java)
                        intent.putExtra("image_uri", currentImageUri)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            requireContext(), "Gagal mengambil URI gambar.", Toast.LENGTH_SHORT
                        ).show()
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

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile("KTP_", ".jpg", filesDir)
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

    companion object {
        private const val REQUIRED_PERMISSION = "android.permission.CAMERA"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}