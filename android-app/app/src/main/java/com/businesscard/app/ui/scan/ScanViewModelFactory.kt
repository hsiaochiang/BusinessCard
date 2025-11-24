package com.businesscard.app.ui.scan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.businesscard.app.domain.create.CreateContactUseCase

class ScanViewModelFactory(
    private val application: Application,
    private val createContactUseCase: CreateContactUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            val imageStore = ImageFileStore(application)
            val ocrProcessor = OcrProcessor()
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(
                application = application,
                createContactUseCase = createContactUseCase,
                ocrProcessor = ocrProcessor,
                imageStore = imageStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
