package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityScanCardBinding
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IScanResultCallback
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.ListDialog
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.ListDialogWithIcons
import com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras
import com.frosch2010.lifestyle_scoring_app.ui.util.getSerializableExtraCompat
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.ScanCardViewModel
import com.frosch2010.lifestyle_scoring_app.utils.AppLogger
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import com.google.android.material.color.MaterialColors
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * Camera capture flow: previews frames, runs ML Kit OCR, optionally routes through manual follow-ups, then returns a [ScanResult].
 *
 * Result delivery uses [IntentExtras.EXTRA_SCAN_RESULT] on the finishing intent.
 */
@AndroidEntryPoint
class ScanCardActivity : AppCompatActivity(), IScanResultCallback {

    private lateinit var binding: ActivityScanCardBinding
    private lateinit var viewModel: ScanCardViewModel

    /** Hilt-provided text recognizer pipeline shared across the activity lifecycle. */
    @Inject
    lateinit var recognizer: ICardRecognizerService

    /** CameraX capture use case bound after permission grant. */
    private lateinit var imageCapture: ImageCapture

    /** Activity result API entry point for manual add when OCR is skipped or fails. */
    private lateinit var handleManualAddCard: ActivityResultLauncher<Intent>

    /** Currently bound camera for torch/state callbacks; nullable until bind succeeds. */
    private var boundCamera: Camera? = null

    /**
     * Prepares CameraX surfaces, permission launchers, edge-to-edge chrome, and ViewModel-driven dialog chains.
     *
     * @param savedInstanceState Unused; camera pipeline is rebuilt each entry.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanCardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupEdgeToEdge()

        manageCameraPermission()

        viewModel = ViewModelProvider(this)[ScanCardViewModel::class.java]
        imageCapture = ImageCapture.Builder().build()
        initHandler()

        initView()
        observeViewModel()
        applyStatusBarColor()
    }

    /**
     * Wires navigation, manual-add launcher, and shutter button to the capture pipeline.
     */
    private fun initView() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnManualAdd.setOnClickListener {
            val intent = Intent(this, ManualAddCardActivity::class.java)
            handleManualAddCard.launch(intent)
        }
        binding.cameraCaptureButton.setOnClickListener {
            setScanningUiActive(true)
            scan()
        }
    }

    /**
     * Toggles progress UI vs live preview while a capture or OCR pass is in flight.
     *
     * @param active `true` hides preview and shows spinner; `false` restores preview.
     */
    private fun setScanningUiActive(active: Boolean) {
        if (active) {
            binding.txtCameraError.visibility = View.GONE
        }
        binding.scanProgressBar.visibility = if (active) View.VISIBLE else View.GONE
        binding.scanningLabel.visibility = if (active) View.VISIBLE else View.GONE
        binding.cameraPreview.visibility = if (active) View.GONE else View.VISIBLE
    }

    /** Hides the persistent error banner after a successful camera open. */
    private fun clearPersistentCameraError() {
        binding.txtCameraError.visibility = View.GONE
    }

    /**
     * Surfaces a multi-line error with optional emulator hint and retry guidance.
     *
     * @param primaryMessageRes Headline string resource id for the failure category.
     */
    private fun showPersistentCameraError(primaryMessageRes: Int) {
        val parts = mutableListOf(getString(primaryMessageRes))
        if (isLikelyEmulator()) {
            parts.add(getString(R.string.scan_camera_emulator_hint))
        }
        parts.add(getString(R.string.scan_camera_retry_hint))
        binding.txtCameraError.text = parts.joinToString("\n\n")
        binding.txtCameraError.visibility = View.VISIBLE
        setScanningUiActive(false)
    }

    /**
     * @return Heuristic guess whether CameraX is running on an emulator with flaky camera HAL behavior.
     */
    private fun isLikelyEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk", ignoreCase = true)
            || Build.MODEL.contains("Emulator", ignoreCase = true)
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || Build.PRODUCT == "google_sdk"
    }

    /**
     * Subscribes to fatal camera errors so binding failures surface as inline guidance instead of silent logs.
     *
     * @param camera Bound camera instance whose [androidx.camera.core.CameraState] stream is observed.
     */
    private fun observeCameraState(camera: Camera) {
        camera.cameraInfo.cameraState.observe(this) { state ->
            when (state.type) {
                CameraState.Type.OPEN -> clearPersistentCameraError()
                else -> Unit
            }
            state.error?.let { err ->
                AppLogger.e(TAG, "Camera state error: code=${err.code}", err.cause)
                showPersistentCameraError(R.string.scan_camera_error_generic)
            }
        }
    }

    /**
     * Binds preview + [imageCapture] to the back camera on the main thread and re-subscribes state observers.
     */
    private fun startCamera() {
        val mainExecutor = ContextCompat.getMainExecutor(this)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.surfaceProvider = binding.cameraPreview.surfaceProvider }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                boundCamera?.cameraInfo?.cameraState?.removeObservers(this@ScanCardActivity)
                boundCamera = null
                cameraProvider.unbindAll()

                // Bind use cases to camera
                boundCamera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                boundCamera?.let { observeCameraState(it) }

            } catch (exc: Exception) {
                AppLogger.e(TAG, "Use case binding failed", exc)
                mainExecutor.execute {
                    showPersistentCameraError(R.string.scan_camera_binding_failed)
                }
            }

        }, mainExecutor)
    }

    /**
     * Captures a still frame and forwards it to [ImageCapturedCallback] for ML Kit processing on the main executor.
     */
    private fun scan() {
        val mainExecutor = ContextCompat.getMainExecutor(this)
        imageCapture.takePicture(
            mainExecutor,
            ImageCapturedCallback(
                recognizer = recognizer,
                callback = this,
                mainExecutor = mainExecutor,
                hideScanningUi = { setScanningUiActive(false) },
                appContext = this,
                onFatalCaptureFailure = {
                    showPersistentCameraError(R.string.scan_camera_capture_failed)
                }
            )
        )
    }

    /**
     * Registers the manual-add activity contract and pipes the chosen [ICard] back into the ViewModel pipeline.
     */
    private fun initHandler() {
        handleManualAddCard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != RESULT_OK || result.data == null) {
                return@registerForActivityResult
            }

            val selectedCard =
                result.data?.getSerializableExtraCompat<ICard>(ManualAddCardActivity.EXTRA_SELECTED_CARD)
                ?: return@registerForActivityResult

            viewModel.onCardSelected(selectedCard)
        }
    }

    /**
     * Observes one-shot ViewModel events to show pickers, toasts, or finish with a [ScanResult].
     */
    private fun observeViewModel() {
        viewModel.events.observe(this) { event ->
            when (val uiEvent = event.consume()) {
                is ScanCardViewModel.UiEvent.ShowNoCardRecognized -> {
                    Toast.makeText(this, getString(R.string.scan_no_card_recognized), Toast.LENGTH_SHORT).show()
                }
                is ScanCardViewModel.UiEvent.AskForCarPoints -> {
                    ListDialog(
                        this,
                        getString(R.string.how_many_points),
                        uiEvent.choices.map { it.toString() },
                        object : ListDialog.OnItemSelectedListener {
                            override fun onItemSelected(item: String) {
                                viewModel.onCarPointsSelected(uiEvent.card, item.toInt(), uiEvent.success)
                            }
                        }
                    ).show()
                }
                is ScanCardViewModel.UiEvent.AskForLoveType -> {
                    ListDialogWithIcons(
                        this,
                        getString(R.string.what_type_of_love),
                        uiEvent.options,
                        object : ListDialogWithIcons.OnItemSelectedListener {
                            override fun onItemSelected(item: String) {
                                viewModel.onLoveTypeSelected(uiEvent.card, item, uiEvent.success)
                            }
                        }
                    ).show()
                }
                is ScanCardViewModel.UiEvent.DeliverResult -> deliverScanResult(uiEvent.result)
                null -> Unit
            }
        }
    }

    /**
     * @param context Context whose `checkSelfPermission` is queried for every entry in [REQUIRED_PERMISSIONS].
     * @return `true` when camera permission is granted for this process.
     */
    private fun allPermissionsGranted(context: Context) =
        REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    /**
     * Starts CameraX when permission is already granted, otherwise triggers the legacy permission request path.
     */
    private fun manageCameraPermission() {
        if (allPermissionsGranted(this)) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    /**
     * @param requestCode Must match [REQUEST_CODE_PERMISSIONS] for camera flow continuation.
     * @param permissions Permission identifiers returned by the system (unused beyond super call).
     * @param grantResults Parallel grant results for [permissions].
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted(this)) {
            startCamera()
        } else {
            Toast.makeText(this, getString(R.string.no_camera_permissions), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    /**
     * Bridges CameraX still capture to ML Kit by converting the buffer to [InputImage] on the success path.
     *
     * @param recognizer OCR service producing a nullable [com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard].
     * @param callback Activity implementing [IScanResultCallback] for synchronous hand-off to ViewModel orchestration.
     * @param mainExecutor Executor used for recognition task listeners.
     * @param hideScanningUi Restores preview/progress UI when recognition completes or fails.
     * @param appContext Long-lived context for toasts outside the activity window token edge cases.
     * @param onFatalCaptureFailure Shows persistent error UI when capture cannot produce a decodable buffer.
     */
    class ImageCapturedCallback(
        private val recognizer: ICardRecognizerService,
        private val callback: IScanResultCallback,
        private val mainExecutor: Executor,
        private val hideScanningUi: () -> Unit,
        private val appContext: Context,
        private val onFatalCaptureFailure: () -> Unit
    ) : ImageCapture.OnImageCapturedCallback() {

        @ExperimentalGetImage
        /**
         * @param imageProxy Camera frame closed after conversion to avoid leaking ImageReader buffers.
         */
        override fun onCaptureSuccess(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = convertImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                imageProxy.close()
                recognizer.process(image)
                    .addOnSuccessListener(mainExecutor) { card ->
                        callback.onScanResult(ScanResult(card != null, card))
                    }
                    .addOnFailureListener(mainExecutor) { e ->
                        AppLogger.e(TAG, "Recognition failed", e)
                        hideScanningUi()
                        Toast.makeText(
                            appContext,
                            appContext.getString(R.string.scan_no_card_recognized),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                imageProxy.close()
                hideScanningUi()
            }
        }

        /**
         * Wraps the camera [Image] in an [InputImage], falling back to a JPEG round-trip when `fromMediaImage` rejects the buffer.
         *
         * @param mediaImage YUV/NV21 camera buffer from CameraX.
         * @param rotationDegrees Sensor orientation applied to ML Kit metadata.
         * @return Non-null [InputImage] suitable for [ICardRecognizerService.process].
         */
        private fun convertImage(mediaImage: Image, rotationDegrees: Int): InputImage {
            return try {
                InputImage.fromMediaImage(mediaImage, rotationDegrees)
            } catch (e: IllegalArgumentException) {
                AppLogger.e(TAG, "Could not parse image from media image, try workaround", e)
                val buffer: ByteBuffer = mediaImage.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                InputImage.fromBitmap(bitmapImage, rotationDegrees)
            }
        }

        /**
         * @param exception CameraX failure describing why no frame was produced.
         */
        override fun onError(exception: ImageCaptureException) {
            AppLogger.e(TAG, "Image capture failed", exception)
            hideScanningUi()
            mainExecutor.execute { onFatalCaptureFailure() }
        }
    }

    /**
     * [IScanResultCallback] entry from [ImageCapturedCallback] once OCR resolves a card candidate.
     *
     * @param result Success flag plus nullable card payload forwarded into the ViewModel state machine.
     */
    override fun onScanResult(result: ScanResult) {
        setScanningUiActive(false)
        viewModel.onScanCompleted(result)
    }

    /**
     * Finishes the activity returning [IntentExtras.EXTRA_SCAN_RESULT] to the caller on [Activity.RESULT_OK].
     *
     * @param result Final payload after optional user disambiguation dialogs.
     */
    private fun deliverScanResult(result: ScanResult) {
        val intent = Intent()
        intent.putExtra(IntentExtras.EXTRA_SCAN_RESULT, result)
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * Applies transparent navigation, toolbar inset expansion, and bottom-aligned action margins for CameraX preview.
     */
    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = !isDarkMode()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !isDarkMode()

        val initialToolbarPaddingStart = binding.toolbar.paddingStart
        val initialToolbarPaddingTop = binding.toolbar.paddingTop
        val initialToolbarPaddingEnd = binding.toolbar.paddingEnd
        val initialToolbarPaddingBottom = binding.toolbar.paddingBottom
        val initialToolbarHeight = binding.toolbar.layoutParams.height

        val initialHelpCardMarginStart = (binding.helpCard.layoutParams as ViewGroup.MarginLayoutParams).marginStart
        val initialHelpCardMarginEnd = (binding.helpCard.layoutParams as ViewGroup.MarginLayoutParams).marginEnd

        val initialActionsMarginStart = (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).marginStart
        val initialActionsMarginEnd = (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
        val initialActionsMarginBottom = (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.toolbar.setPadding(
                initialToolbarPaddingStart + bars.left,
                initialToolbarPaddingTop + bars.top,
                initialToolbarPaddingEnd + bars.right,
                initialToolbarPaddingBottom
            )
            binding.toolbar.layoutParams = binding.toolbar.layoutParams.apply {
                height = initialToolbarHeight + bars.top
            }

            (binding.helpCard.layoutParams as ViewGroup.MarginLayoutParams).apply {
                marginStart = initialHelpCardMarginStart + bars.left
                marginEnd = initialHelpCardMarginEnd + bars.right
                binding.helpCard.layoutParams = this
            }

            binding.cameraPreview.setPadding(bars.left, 0, bars.right, bars.bottom)

            (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).apply {
                marginStart = initialActionsMarginStart + bars.left
                marginEnd = initialActionsMarginEnd + bars.right
                bottomMargin = initialActionsMarginBottom + bars.bottom
                binding.layoutActions.layoutParams = this
            }

            applyStatusBarColor()
            insets
        }
    }

    /** Sets status bar color to match the elevated toolbar surface for consistent edge-to-edge appearance. */
    private fun applyStatusBarColor() {
        val topBarColor = MaterialColors.getColor(binding.toolbar, com.google.android.material.R.attr.colorSurfaceContainer)
        window.statusBarColor = topBarColor
    }

    /**
     * @return `true` when night mode is active for system bar icon contrast on the scan screen.
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Unbinds CameraX use cases and drops camera state observers to avoid leaks when leaving the capture flow.
     */
    override fun onDestroy() {
        boundCamera?.cameraInfo?.cameraState?.removeObservers(this)
        boundCamera = null
        try {
            ProcessCameraProvider.getInstance(this).get().unbindAll()
        } catch (e: Exception) {
            AppLogger.w(TAG, "unbindAll in onDestroy", e)
        }
        super.onDestroy()
    }

    companion object {
        /** Log tag for CameraX and ML Kit diagnostics. */
        const val TAG = "CameraXBasic"

        /** Legacy request code for [onRequestPermissionsResult] camera permission flow. */
        const val REQUEST_CODE_PERMISSIONS = 10

        /** Runtime permissions required before binding preview and image capture use cases. */
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
