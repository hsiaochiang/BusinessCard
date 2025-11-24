package com.businesscard.app.ui.scan

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageFileStore(private val context: Context) {
    fun save(bitmap: Bitmap): String {
        val file = File(context.cacheDir, "scan-${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        return file.absolutePath
    }

    private companion object {
        const val JPEG_QUALITY = 90
    }
}
