package com.businesscard.app.data.remote.drive

interface DriveClient {
    suspend fun upload(localPath: String, targetName: String): DriveUploadResult
}

sealed interface DriveUploadResult {
    data class Success(val sharedUrl: String) : DriveUploadResult
    data class Failure(val reason: String) : DriveUploadResult
}
