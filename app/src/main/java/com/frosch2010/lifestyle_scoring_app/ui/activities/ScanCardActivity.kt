package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityScanCardBinding
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.impl.CardRecognizerService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IScanResultCallback
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.ScanCardViewModel
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.nio.ByteBuffer


@AndroidEntryPoint
class ScanCardActivity : AppCompatActivity(), IScanResultCallback {

    private lateinit var binding: ActivityScanCardBinding

    private lateinit var viewModel: ScanCardViewModel

    private lateinit var recognizer: ICardRecognizerService
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanCardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        manageCameraPermission()

        viewModel = ViewModelProvider(this)[ScanCardViewModel::class.java]
        recognizer = CardRecognizerService(viewModel.getCardMap())
        imageCapture = ImageCapture.Builder().build()

        initView()
    }

    private fun initView(){
        binding.cameraCaptureButton.setOnClickListener {
            binding.scanProgressBar.visibility = View.VISIBLE
            binding.scanningLabel.visibility = View.VISIBLE
            binding.cameraPreview.visibility = View.GONE
            scan()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e(Camera.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun scan() {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            ImageCapturedCallback(this, recognizer, viewModel, this)
        )
    }

    object Camera {
        const val TAG = "CameraXBasic"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun allPermissionsGranted(context: Context) = Camera.REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    private fun manageCameraPermission() {
        if (allPermissionsGranted(this)) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                Camera.REQUIRED_PERMISSIONS,
                Camera.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    class ImageCapturedCallback(private val activity: Activity, private val recognizer: ICardRecognizerService, private val viewModel: ScanCardViewModel, private val callback: IScanResultCallback) : ImageCapture.OnImageCapturedCallback() {

        @ExperimentalGetImage
        override fun onCaptureSuccess(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = convertImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                imageProxy.close()
                recognizer.process(image).addOnSuccessListener {
                    callback.onScanResult(ScanResult(!viewModel.showDialogNoCardRecognized(it), it))
                }
            }
        }

        /**
         * Convert image to InputImage<br>
         * This method as a workaround so solve this issue : https://github.com/benjamin-klamerek/fantasy-realm-scoring/issues/12<br>
         * I cannot guarantee that the problem is solved. But at least, app crash is avoided<br>
         */
        private fun convertImage(mediaImage: Image, rotationDegrees: Int): InputImage {
            return try {
                InputImage.fromMediaImage(mediaImage, rotationDegrees)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Could not parse image from media image, try workaround", e)
                val buffer: ByteBuffer = mediaImage.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                InputImage.fromBitmap(bitmapImage, rotationDegrees)
            }
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e(TAG, "Use case binding failed", exception)
        }
    }

    override fun onScanResult(result: ScanResult) {
        if(!result.success){
            // TODO show dialog

            val intent = Intent()
            intent.putExtra("scan_result", result)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }else{
            // TODO close Activity and send result back

            val intent = Intent()
            intent.putExtra("scan_result", result)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}