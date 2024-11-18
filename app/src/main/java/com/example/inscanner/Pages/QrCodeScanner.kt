package com.example.inscanner.Pages

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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

    AndroidView(factory = { previewView }) { view ->
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
                cameraProvider.bindToLifecycle(
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
