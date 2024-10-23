package com.example.datasetapp.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
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
import com.example.datasetapp.R
import com.example.datasetapp.databinding.FragmentCameraKtpBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraKtpFragment : Fragment() {

    private lateinit var mBinding: FragmentCameraKtpBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var currentImageUri: Uri? = null
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCameraKtpBinding.inflate(layoutInflater,container, false)
        mBinding.captureImage.setOnClickListener { takePhoto() }

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
//        globalViewModel.ktpImageUri.observe(viewLifecycleOwner) {
//            if (it == null) {
                startCamera()
//                mBinding.viewFinder.show()
//                mBinding.captureImage.show()
//                mBinding.ivPreview.hide()
//                mBinding.btnRetry.hide()

//            } else {
//                mBinding.ivPreview.setImageURI(it)
//                mBinding.viewFinder.hide()
//                mBinding.captureImage.hide()
//                mBinding.ivPreview.show()
//                mBinding.btnRetry.show()
//            }
//        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build().also {
                    it.setSurfaceProvider(mBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val viewPort =  ViewPort.Builder(Rational(350, 200),  Surface.ROTATION_0 ).build()

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
//                    globalViewModel.ktpImageUri.value = output.savedUri

                    encodeImage()
                    mBinding.viewFinder
                    mBinding.captureImage
                    Toast.makeText(
                        requireContext(), "Berhasil mengambil gambar.", Toast.LENGTH_SHORT
                    ).show()
                    stopCamera()
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
        return File.createTempFile(timeStamp, ".jpg", filesDir)
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
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}