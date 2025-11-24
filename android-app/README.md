# Business Card Android App

本模組為名片管理系統 Android 端，負責掃描→OCR 預填→暫存→上傳試算表與影像。

## 快速開始（本機）

1. 安裝 Android Studio（Kotlin 1.9.x、Android SDK 34，minSdk 29）。
2. 匯入 `android-app/`，同步 Gradle wrapper。
3. 於 `app/` 下建立 `google-services.json`（可先複製 `google-services.json.example` 並填入測試值）。
4. 建置 Debug：`./gradlew :app:assembleDebug`
5. 安裝 side-load APK：`adb install -r app/build/outputs/apk/debug/app-debug.apk`

## 品質檢查

- Lint/Detekt：`./gradlew :app:detekt`
- 單元測試與覆蓋率報表（Jacoco）：`./gradlew :app:testDebugUnitTest :app:jacocoTestReport`
- 檢查（會串 detekt + 覆蓋率驗證，覆蓋率目前設定寬鬆 baseline 0.1，後續可拉高）：`./gradlew :app:check`

## 環境變數 / 設定 placeholder

- `ANDROID_RELEASE_KEYSTORE_PATH`
- `GOOGLE_SERVICES_JSON_PATH`
- `OAUTH_CLIENT_CONFIG_PATH`

上述敏感設定透過環境變數或外部路徑提供，不應提交版本庫。
