package com.example.aisnaptutor.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.aisnaptutor.R
import com.example.aisnaptutor.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import ke.derrick.imagetotext.viewmodels.SharedViewModel
import java.io.File
import java.io.FileOutputStream


class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null
    private var isImageView = false
    private lateinit var viewModel: SharedViewModel
    var rotationAngle = 0
    lateinit var m: Image
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("WrongThread")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        if (viewModel.camOrGal == 2) {
            imageUri = Uri.parse(viewModel.selectedImageUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val image = InputImage.fromBitmap(bitmap, 0)
                toggleImageView1(image, true)
            } else {
                val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val image = InputImage.fromBitmap(bitmap, 0)
                toggleImageView1(image, true)
            }
        } else {
            startCamera()
        }

        binding.imageCaptureBtn.setOnClickListener {
            takePhoto()
        }
        binding.btnRotate.setOnClickListener {
            rotationAngle += 90
            binding.cropImageView.rotation = rotationAngle.toFloat()
        }

        binding.btnGenerateTxt.setOnClickListener {
            if (viewModel.camOrGal == 1) {
                val cropImageView = binding.cropImageView
                val cropped: Bitmap? = cropImageView.getCroppedImage()
                val image = InputImage.fromBitmap(cropped!!, 0)
                runTextRecognition(image)
            } else {
                val cropImageView = binding.cropImageView
                val cropped: Bitmap? = cropImageView.getCroppedImage()
                val image = InputImage.fromBitmap(cropped!!, 0)
                runTextRecognition(image)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
            }, 1000)
        }

        binding.btnGallery1.setOnClickListener {
            openGallery()
        }
        binding.btnFlash.setOnClickListener {
            toggleFlashlight()
        }
        binding.btnBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, HomeFragment.REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                HomeFragment.REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    imageUri = selectedImageUri!!
                    viewModel.selectedImageUri = imageUri.toString()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val source =
                            ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        val image = InputImage.fromBitmap(bitmap, 0)
                        toggleImageView1(image, true)
                    } else {
                        val inputStream =
                            requireActivity().contentResolver.openInputStream(imageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val image = InputImage.fromBitmap(bitmap, 0)
                        toggleImageView1(image, true)
                    }
                }
            }
        }
    }

    private fun toggleFlashlight() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector)
                val cameraControl = camera.cameraControl
                val torchState = camera.cameraInfo.torchState.value
                if (torchState == TorchState.OFF) {
                    cameraControl.enableTorch(true)
                } else {
                    cameraControl.enableTorch(false)
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            @ExperimentalGetImage object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    m = image.image!!
                    val image = InputImage.fromMediaImage(m, 0)
                    toggleImageView1(image, true)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.d(TAG, "Image not captured")
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.preview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun runTextRecognition(inputImage: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(inputImage)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun toggleImageView1(image: InputImage?, imageView: Boolean) {
        isImageView = imageView
        if (imageView) {
            binding.preview.visibility = View.GONE
            binding.constraintLayout2.visibility = View.GONE
            binding.parentCrop.visibility = View.VISIBLE
            binding.txtCropAndRotate.visibility = View.VISIBLE
            binding.imageView.visibility = View.GONE
            binding.cropImageView.setImageBitmap(image!!.bitmapInternal)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    val camera = cameraProvider.bindToLifecycle(this, cameraSelector)
                    val cameraControl = camera.cameraControl
                    cameraControl.enableTorch(false)
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } else {
            binding.parentCrop.visibility = View.GONE
            binding.preview.visibility = View.VISIBLE
            binding.txtCropAndRotate.visibility = View.GONE
            binding.imageView.visibility = View.GONE
            binding.constraintLayout2.visibility = View.VISIBLE
        }

        ActivityCompat.invalidateOptionsMenu(requireContext() as Activity)
    }

    private fun processTextRecognitionResult(texts: Text) {
        val blocks: List<Text.TextBlock> = texts.textBlocks

        if (blocks.isEmpty()) {
            Log.d(TAG, "No text found")
            Toast.makeText(requireContext(), "No text found", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.setTextBlocks(blocks)
        for (i in blocks.indices) {
            val lines: List<Text.Line> = blocks[i].lines
            for (j in lines.indices) {
                val elements: List<Text.Element> = lines[j].elements
                for (k in elements.indices) {
//                    val textGraphic: GraphicOverlay.Graphic = TextGraphic(binding.graphicOverlay, elements[k])
//                    binding.graphicOverlay.add(textGraphic)
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                (requireContext() as Activity).finish()
            }
        }
    }

    companion object {
        private const val TAG = "MainFragment"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}