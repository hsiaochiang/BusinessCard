package com.businesscard.app.fakes

import com.businesscard.app.data.remote.drive.DriveClient
import com.businesscard.app.data.remote.drive.DriveUploadResult

class FakeDriveClient : DriveClient {
    val uploads = mutableListOf<Pair<String, String>>()
    override suspend fun upload(localPath: String, targetName: String): DriveUploadResult {
        uploads.add(localPath to targetName)
        return DriveUploadResult.Success(sharedUrl = "drive://$targetName")
    }
}
