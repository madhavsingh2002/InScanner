package com.mkl.inscanner.Pages

import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.DecodeHintType
import com.google.zxing.BarcodeFormat
import androidx.core.content.ContextCompat
import com.mkl.inscanner.R


@Composable
fun QRCodeScanner(
    onQRCodeScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val previewView = remember { PreviewView(context) }
    val qrCodeAnalyzer = remember {
        QRCodeAnalyzer { qrCode ->
            onQRCodeScanned(qrCode)
        }
    }
    var zoomState by remember { mutableStateOf(1f) } // Zoom level
    var camera: Camera? by remember { mutableStateOf(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            decodeQRCodeFromUri(uri, context, onQRCodeScanned)
        }
    }
    var isTorchOn by remember { mutableStateOf(false) }
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = cameraManager.cameraIdList.find { id ->
        cameraManager.getCameraCharacteristics(id)
            .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    }

    fun toggleTorch(turnOn: Boolean) {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(cameraId, turnOn)
                isTorchOn = turnOn
            } catch (e: CameraAccessException) {
                when (e.reason) {
                    CameraAccessException.CAMERA_IN_USE -> Log.e("Torch", "Camera in use by another process")
                    else -> Log.e("Torch", "Error toggling torch", e)
                }
            } catch (e: Exception) {
                Log.e("Torch", "Unexpected error", e)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.imageicon),
                contentDescription = "Image icon",
                modifier = Modifier
                    .clickable {
                        pickImageLauncher.launch("image/*")
                    }
                    .size(32.dp) // Adjust size as needed
            )
            Spacer(modifier = Modifier.width(32.dp))
            Image(
                painter = painterResource(
                    id = if (isTorchOn) R.drawable.flash_on else R.drawable.flash_off
                ),
                contentDescription = "change",
                modifier = Modifier
                    .clickable {
                        toggleTorch(!isTorchOn)
                    }
                    .size(32.dp) // Adjust size as needed
            )

            Spacer(modifier = Modifier.width(32.dp))
            Image(
                painter = painterResource(id = R.drawable.flip_camera),
                contentDescription = "Flip Camera",
                modifier = Modifier
                    .clickable {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(ContextCompat.getMainExecutor(context), qrCodeAnalyzer)
                                }

                            try {
                                cameraProvider.unbindAll()
                                camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (exc: Exception) {
                                Log.e("QRCodeScanner", "Use case binding failed", exc)
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                    .size(32.dp) // Adjust size as needed
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            zoomState = (zoomState * zoom).coerceIn(1f, camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f)
                            camera?.cameraControl?.setZoomRatio(zoomState)
                        }
                    }
            ) { view ->
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(view.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(context), qrCodeAnalyzer)
                        }

                    try {
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("QRCodeScanner", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context))
            }

            // Scanning Box Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width * 0.6f
                val height = size.height * 0.3f
                val left = (size.width - width) / 2
                val top = (size.height - height) / 2

                drawRect(
                    color = Color(0xFFD9D9D9),
                    topLeft = androidx.compose.ui.geometry.Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(width, height),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(androidx.compose.ui.Alignment.BottomCenter),
                verticalArrangement = Arrangement.Top
            ) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    ZoomButton("Zoom In") {
                        zoomState = (zoomState + 0.1f).coerceIn(1f, camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f)
                        camera?.cameraControl?.setZoomRatio(zoomState)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    ZoomButton("Zoom Out") {
                        zoomState = (zoomState - 0.1f).coerceIn(1f, camera?.cameraInfo?.zoomState?.value?.minZoomRatio ?: 1f)
                        camera?.cameraControl?.setZoomRatio(zoomState)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF208E67),
            contentColor = Color.White,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

class QRCodeAnalyzer(
    private val onQRCodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader()

    init {
        reader.setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    override fun analyze(imageProxy: ImageProxy) {
        val buffer = imageProxy.planes.first().buffer
        val data = ByteArray(buffer.remaining()).apply { buffer.get(this) }
        val width = imageProxy.width
        val height = imageProxy.height

        val source = PlanarYUVLuminanceSource(
            data, width, height, 0, 0, width, height, false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = reader.decode(bitmap)
            onQRCodeDetected(result.text)
        } catch (e: NotFoundException) {
            // QR Code not found in frame
        } finally {
            imageProxy.close()
        }
    }
}
fun decodeQRCodeFromUri(
    uri: Uri,
    context: android.content.Context,
    onQRCodeScanned: (String) -> Unit
) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val width = bitmap.width
        val height = bitmap.height

        // Convert the bitmap into a grayscale ByteArray
        val byteArray = ByteArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (0.299 * (pixel shr 16 and 0xFF) +
                        0.587 * (pixel shr 8 and 0xFF) +
                        0.114 * (pixel and 0xFF)).toInt()
                byteArray[y * width + x] = gray.toByte()
            }
        }

        val source = PlanarYUVLuminanceSource(
            byteArray,
            width,
            height,
            0,
            0,
            width,
            height,
            false
        )
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val result = MultiFormatReader().decode(binaryBitmap)
        onQRCodeScanned(result.text)
    } catch (e: Exception) {
        Log.e("QRCodeScanner", "Error decoding QR Code", e)
    }
}
