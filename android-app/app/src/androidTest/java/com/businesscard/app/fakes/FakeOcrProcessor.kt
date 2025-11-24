package com.businesscard.app.fakes

import android.graphics.Bitmap
import com.businesscard.app.ui.scan.OcrProcessor
import com.businesscard.app.ui.scan.OcrResult

class FakeOcrProcessor(private val result: OcrResult) : OcrProcessor() {
    override suspend fun process(bitmap: Bitmap): OcrResult = result
}
