package com.businesscard.app.data.remote.drive

import com.businesscard.app.telemetry.Telemetry

class DriveUploader(
    private val telemetry: Telemetry
) : DriveClient {
    override suspend fun upload(localPath: String, targetName: String): DriveUploadResult {
        // 待後續串接 Drive 上傳與共用 URL 取得
        telemetry.recordUploadLatency(DEFAULT_LATENCY_MS)
        return DriveUploadResult.Success(sharedUrl = "drive://$targetName")
    }

    private companion object {
        const val DEFAULT_LATENCY_MS = 0L
    }
}
