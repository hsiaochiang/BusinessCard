package com.businesscard.app.ui.scan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    fun onImageCaptured(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = ScanUiState.Error("未取得影像")
            return
        }
        _uiState.value = ScanUiState.Processing
        // 後續會串接 OCR 與暫存流程
        viewModelScope.launch {
            _uiState.value = ScanUiState.ReadyForUpload(preview = bitmap)
        }
    }

    fun onUploadTriggered() {
        _uiState.value = ScanUiState.Uploading
        // 後續會串接 WorkManager 佇列
        viewModelScope.launch {
            _uiState.value = ScanUiState.Uploaded
        }
    }

    fun reset() {
        _uiState.value = ScanUiState.Idle
    }
}

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Processing : ScanUiState
    data class ReadyForUpload(val preview: Bitmap) : ScanUiState
    data object Uploading : ScanUiState
    data object Uploaded : ScanUiState
    data class Error(val message: String) : ScanUiState
}
