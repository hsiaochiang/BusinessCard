package com.businesscard.app.ui.scan

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.businesscard.app.data.repository.ContactDraftForm
import com.businesscard.app.domain.create.CreateContactUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(
    application: Application,
    private val createContactUseCase: CreateContactUseCase,
    private val ocrProcessor: OcrProcessor,
    private val imageStore: ImageFileStore
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    fun onImageCaptured(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = ScanUiState.Error("未取得影像")
            return
        }
        _uiState.value = ScanUiState.Processing
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val savedPath = imageStore.save(bitmap)
                val ocr = ocrProcessor.process(bitmap)
                val form = ContactDraftForm(
                    name = ocr.name.ifBlank { "未命名名片" },
                    company = ocr.company,
                    title = ocr.title,
                    email = ocr.email,
                    phone = ocr.phone,
                    tags = ocr.tags,
                    notes = ocr.notes,
                    imagePath = savedPath
                )
                runCatching { createContactUseCase.saveDraft(form) }
            }
            result.onSuccess {
                _uiState.value = ScanUiState.Uploading
                _uiState.value = ScanUiState.Uploaded
            }.onFailure { ex ->
                _uiState.value = ScanUiState.Error(ex.message ?: "上傳排程失敗")
            }
        }
    }

    fun onUploadTriggered() {
        _uiState.value = ScanUiState.Uploading
        createContactUseCase.enqueueUploadOnly()
        _uiState.value = ScanUiState.Uploaded
    }

    fun reset() {
        _uiState.value = ScanUiState.Idle
    }
}

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Processing : ScanUiState
    data object Uploading : ScanUiState
    data object Uploaded : ScanUiState
    data class Error(val message: String) : ScanUiState
}
