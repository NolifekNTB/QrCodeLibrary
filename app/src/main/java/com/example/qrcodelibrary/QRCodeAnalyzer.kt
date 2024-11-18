package com.example.qrcodelibrary

import android.graphics.ImageFormat
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader

class QRCodeAnalyzer(
    private val onQRCode: (String) -> Unit,
    private val shouldContinueScanning: () -> Boolean
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (!shouldContinueScanning()) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null && imageProxy.format == ImageFormat.YUV_420_888) {
            val cropRect = imageProxy.cropRect
            val source = ImageProxyLuminanceSource(mediaImage, cropRect)
            val bitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val reader = QRCodeMultiReader()
                val result = reader.decode(bitmap)
                onQRCode(result.text)
            } catch (e: Exception) {
                // Handle cases where no QR code is detected
            } finally {
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }
}
