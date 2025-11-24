package com.businesscard.app.ui.scan

import android.graphics.Bitmap

data class OcrResult(
    val name: String = "",
    val company: String? = null,
    val title: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

class OcrProcessor {
    suspend fun process(bitmap: Bitmap): OcrResult {
        // TODO: 串接 ML Kit OCR，回傳辨識結果
        return OcrResult()
    }
}
