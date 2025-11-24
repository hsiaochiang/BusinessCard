package com.businesscard.app.data.remote.drive

import com.businesscard.app.telemetry.Telemetry

class DriveUploader(
    private val telemetry: Telemetry
) : DriveClient {
    override suspend fun upload(localPath: String, targetName: String): DriveUploadResult {
        // TODO: 實際串接 Drive 上傳與共用 URL 取得
        telemetry.recordUploadLatency(0)
        return DriveUploadResult.Success(sharedUrl = "drive://$targetName")
    }
}
