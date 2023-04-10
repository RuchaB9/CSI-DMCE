package com.example.csi_dmce.attendance

import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csi_dmce.R
import com.example.csi_dmce.database.EventWrapper
import com.example.csi_dmce.utils.GenerateQRHelper
import kotlinx.coroutines.runBlocking
import java.io.IOException

class AdminViewQR : AppCompatActivity() {

    private lateinit var first_qr : ImageButton
    private lateinit var second_qr : ImageButton
    private lateinit var image_bg1 : Bitmap
    private lateinit var image_bg2 : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_view_qr)

        first_qr = findViewById(R.id.first_qr_code)
        second_qr = findViewById(R.id.second_qr_code)

        val eventID = "CSCT-1678280388"
        //val eventID = intent.getStringExtra("event_ID")

        val event_uuid = runBlocking {EventWrapper.getEventUUID(eventID).toString() }

        image_bg1 = GenerateQRHelper.generateQr("FIRST_" + event_uuid, Pair(512,512))
        first_qr.setImageBitmap(image_bg1)

        image_bg2 = GenerateQRHelper.generateQr("SECOND_" + event_uuid, Pair(512,512))
        second_qr.setImageBitmap(image_bg2)

        first_qr.setOnClickListener {
            downloadToGallery(image_bg1)
        }

        second_qr.setOnClickListener {
            downloadToGallery(image_bg2)
        }
    }

    fun downloadToGallery(bitmap: Bitmap) : Void? {
        val filename = "my_image.jpg"
        val mimeType = "image/jpeg"
        val relativePath = "${Environment.DIRECTORY_PICTURES}/MyFolder"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = applicationContext.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values)

        uri?.let { uri ->
            try {
                val outputStream = resolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream?.flush()
                outputStream?.close()
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                MediaScannerConnection.scanFile(applicationContext, arrayOf(uri.toString()), null, null)
                Toast.makeText(this, "Image downloaded!!", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                resolver.delete(uri, null, null)
                Toast.makeText(this, "Download failed.", Toast.LENGTH_SHORT).show()
            }
        }
        return null

    }


}