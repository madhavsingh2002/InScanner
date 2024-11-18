package com.example.inscanner.Pages

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.DecodeHintType
import com.google.zxing.BarcodeFormat
import java.util.concurrent.Executor
import androidx.core.content.ContextCompat

@Composable
fun QRCodeScanner(
    onQRCodeScanned: (String) -> Unit
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
                color = Color.Green,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(width, height),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Zoom Controls
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
                Spacer(modifier = Modifier.width(8.dp))
                ZoomButton("Zoom Out") {
                    zoomState = (zoomState - 0.1f).coerceIn(1f, camera?.cameraInfo?.zoomState?.value?.minZoomRatio ?: 1f)
                    camera?.cameraControl?.setZoomRatio(zoomState)
                }
            }
        }
    }
}

@Composable
fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
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
