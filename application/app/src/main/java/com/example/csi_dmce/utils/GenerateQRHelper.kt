package com.example.csi_dmce.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class GenerateQRHelper {
    companion object {
        // qrKind is either "first" or "second"
        fun generateQr(qrData: String, dimensions: Pair<Int, Int>): Bitmap {
            val writer= QRCodeWriter()

            val bitMatrix: BitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            return bmp
        }
    }
}
