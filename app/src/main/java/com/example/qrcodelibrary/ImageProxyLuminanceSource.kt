package com.example.qrcodelibrary

import android.graphics.Rect
import android.media.Image
import com.google.zxing.LuminanceSource

class ImageProxyLuminanceSource(
    private val image: Image,
    private val cropRect: Rect
) : LuminanceSource(cropRect.width(), cropRect.height()) {

    override fun getMatrix(): ByteArray {
        val yBuffer = image.planes[0].buffer // Y plane
        val ySize = yBuffer.remaining()
        val data = ByteArray(ySize)
        yBuffer.get(data)

        val croppedData = ByteArray(cropRect.width() * cropRect.height())
        var inputOffset = cropRect.top * image.width + cropRect.left
        var outputOffset = 0

        for (row in 0 until cropRect.height()) {
            System.arraycopy(
                data,
                inputOffset,
                croppedData,
                outputOffset,
                cropRect.width()
            )
            inputOffset += image.width
            outputOffset += cropRect.width()
        }

        return croppedData
    }

    override fun getRow(y: Int, row: ByteArray?): ByteArray {
        if (y < 0 || y >= cropRect.height()) {
            throw IllegalArgumentException("Requested row is outside the image: $y")
        }
        val data = row ?: ByteArray(cropRect.width())
        val matrix = getMatrix()
        val offset = y * cropRect.width()
        System.arraycopy(matrix, offset, data, 0, cropRect.width())
        return data
    }

    override fun isCropSupported(): Boolean = true
}
