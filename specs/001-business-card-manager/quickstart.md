# Quickstart - 名片管理系統第一版

**目的**：協助開發者在本機/模擬器快速啟動並驗證掃描、同步、離線暫存與權限流程，產出簽署 APK 並以 side-load 安裝（不上架 Google Play）。  
**前置**：已在 `001-business-card-manager` 分支。

## 1. 環境需求
- Android Studio 最新穩定版（含 Kotlin 1.9.x 插件）。
- Android SDK：目標/compile API 34，最低 API 29。
- Google Play services / Google Sign-In 已安裝於測試裝置或模擬器（僅用於 OAuth，不涉 Play Store 上架）。
- 取得 Google Cloud 專案 OAuth Client (Android) 並設定 SHA-1/包名。

## 2. 專案設定
1) 匯入專案並同步 Gradle。  
2) 設定 `google-services.json`（含 OAuth Client）。  
3) 建立或指派 Google 試算表與 Drive 目錄，僅邀請測試帳號；表格需有 `Contacts` 工作表與欄位驗證。  
4) 在 App 設定中指定目標試算表 ID 與 Drive 目錄（或透過授權 UI 選擇）。

## 3. Build 與 side-load 安裝
- 產出簽署 APK（側載用）：`./gradlew assembleRelease`（或對應 flavor），使用專案簽章金鑰。
- 以 ADB 安裝：`adb install -r app/build/outputs/apk/release/app-release.apk`，或在實體機啟用「允許未知來源」後手動安裝 APK。
- 不需進行 Google Play 上架、內容分級或 Play Integrity 設定。

## 4. 執行與驗證
1) 在實體機/模擬器執行 App，完成 Google 授權。  
2) 掃描名片（可使用測試影像），確認 OCR 預填欄位，必要時手動修正後儲存。  
3) 驗證試算表新增一列，`id/source/created_at/updated_at` 正確，並有影像 URL（若上傳成功）。  
4) 於桌面端修改資料，App 重新整理應反映最新內容；同筆更新以最後寫入為準。  
5) 離線模式：關閉網路建立多筆暫存，恢復上線後應逐筆同步並顯示成功/失敗。  
6) 軟刪除：在 App 或試算表標記 `is_deleted=true`，預設查詢應排除，並可於稽核檢視。

## 5. 測試與驗收
- 單元/邏輯：JUnit；離線/重試可用 Robolectric。  
- UI/整合：Espresso + Instrumentation，覆蓋掃描→儲存→同步與桌面更新回讀。  
- 契約/互動：對 Sheets/Drive 呼叫採 fake/mock 或錄製回放，驗證欄位映射與錯誤處理。  
- 覆蓋率目標：關鍵同步模組 >=90% 陳述、>=75% 分支。

## 6. 故障排除
- 授權失敗：確認 SHA-1/包名、邀請帳號與作用中 OAuth Client。  
- 影像上傳失敗：檢查網路或權限，重試後仍失敗應維持資料列並標記待補。  
- `id` 衝突：刷新資料後重試；維持時間戳+隨機碼生成規則以降低機率。
