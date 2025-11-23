# 隞餃?嚗??恣?頂蝯梁洵銝??side-load APK嚗?

**頛詨**嚗spec.md`?plan.md`?research.md`?data-model.md`?contracts/`  
**?蔭?瘙?*嚗?閮摰???畾蛛??潔???箇偷蝵?APK 靘?side-load嚗?瘨?Google Play 銝??
> ????桀?撠撱箇? `android-app/` 撠?嚗誑銝遙??豢?閮 Blocked/TODO嚗? Android 撠?撱箇?敺銵?

## Blocked 任務彙總

| 任務 | Phase | 狀態 | 重點描述 | 路徑/檔案 |
|------|-------|------|----------|-----------|
| T001 | 1 | Blocked | 建置/確認 Android Gradle 專案設定 | android-app/app/build.gradle.kts |
| T002 | 1 | Blocked [P] | 設定 detekt/ktlint 並串接 CI | android-app/app/, .github/workflows/ |
| T003 | 1 | Blocked | 設定簽署 keystore 資訊 | android-app/app/keystore.properties |
| T004 | 1 | Blocked [P] | 匯入 google-services 與 OAuth Client 設定 | android-app/app/google-services.json |
| T005 | 2 | Blocked | 建置 Room ContactRecord/ContactImage schema | android-app/app/src/main/java/com/businesscard/app/data/db |
| T006 | 2 | Blocked | 建置 Sheets/Drive API 封裝與授權流程 | android-app/app/src/main/java/com/businesscard/app/data/remote |
| T007 | 2 | Blocked [P] | 建立統一日誌/錯誤追蹤 | android-app/app/src/main/java/com/businesscard/app/core/logging |
| T008 | 2 | Blocked | 建置同步/偵錯測試骨架 | android-app/app/src/main/java/com/businesscard/app/sync |
| T009 | 2 | Blocked [P] | 設定遙測/事件追蹤介面 | android-app/app/src/main/java/com/businesscard/app/telemetry |
| T010 | 2 | Blocked | 建立 Contacts 試算表欄位驗證腳本 | automation/sheets_drive/contacts_schema_setup.sh |
| T011 | 2 | Blocked | 檢查並設定 Drive/Sheets 權限腳本 | automation/sheets_drive/permissions_check.sh |
| T012 | 2 | Blocked | CI 覆蓋率門檻設定 | .github/workflows/android-ci.yml |
| T013 | 2 | Blocked | 性能監控與警示 (OCR/Sheets/5k 行) | android-app/app/src/main/java/com/businesscard/app/perf/PerformanceMonitor.kt, CI 報表 |
| T014 | 3 | Blocked | US1：建置掃描輸入 + ML Kit OCR UI | android-app/app/src/main/java/com/businesscard/app/ui/scan |
| T015 | 3 | Blocked [P] | US1：建立寫入流程、生成 id、寫 Room 並入同步佇列 | android-app/app/src/main/java/com/businesscard/app/domain/create |
| T016 | 3 | Blocked [P] | US1：影像上傳 Drive 並回寫 URL | android-app/app/src/main/java/com/businesscard/app/data/remote/drive |
| T017 | 3 | Blocked | US1：掃描同步整合測試 | android-app/app/src/androidTest/java/com/businesscard/app/ScanSyncTest.kt |
| T018 | 3 | Blocked | US1：影像上傳失敗重試紀錄與實作 | android-app/app/src/main/java/com/businesscard/app/data/remote/drive/UploadRetrier.kt |
| T019 | 3 | Blocked | US1：上傳失敗資料保留與提示的 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/UploadRetryTest.kt |
| T020 | 4 | Blocked | US2：資料查詢/篩選/排序 | android-app/app/src/main/java/com/businesscard/app/data/remote/sheets/Queries.kt |
| T021 | 4 | Blocked [P] | US2：更新/刪除寫入流程含 updated_at/is_deleted | android-app/app/src/main/java/com/businesscard/app/domain/update |
| T022 | 4 | Blocked | US2：更新同步 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/SheetUpdateSyncTest.kt |
| T023 | 4 | Blocked | US2：端點 id 衝突提示/回復 | android-app/app/src/main/java/com/businesscard/app/sync/ConflictResolver.kt |
| T024 | 4 | Blocked | US2：桌面端寫入/衝突案例 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/SheetConflictTest.kt |
| T025 | 5 | Blocked | US3：OAuth 設定與權限檢查 | android-app/app/src/main/java/com/businesscard/app/auth |
| T026 | 5 | Blocked [P] | US3：衝突處理政策 (寫入者/updated_at 合併) | android-app/app/src/main/java/com/businesscard/app/sync/conflict |
| T027 | 5 | Blocked | US3：權限/一致性 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/AuthConsistencyTest.kt |
| T028 | 5 | Blocked | US3：帳號/權限存取控制 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/AccessControlTest.kt |
| T029 | 6 | Blocked | 產出簽署 Release APK | android-app/app/build.gradle.kts |
| T030 | 6 | Blocked [P] | 測試 side-load 安裝與更新流程 | tests/manual/sideload.md |
| T031 | 6 | Blocked | 發佈後迴歸測試 | android-app/app/src/androidTest/java/com/businesscard/app/ReleaseRegressionTest.kt |
| T032 | 7 | Blocked | 清單流程確認並更新 spec/plan/quickstart | docs/, specs/001-business-card-manager/ |
| T033 | 7 | Blocked [P] | 追蹤性能/容量極限並完成 50k 測試 | android-app/app/src/main/java/com/businesscard/app/perf |
| T034 | 7 | Blocked | 安全強化（權限 token 管理、日誌掩碼、稽核） | android-app/app/src/main/java/com/businesscard/app/security |
| T035 | 7 | Blocked | 每日迴歸腳本與報表 | automation/ci/daily-regression.sh |
| T036 | 7 | Blocked | 異常率警戒與告警設定 | android-app/app/src/main/java/com/businesscard/app/telemetry/AlertingConfig.kt, automation/alerts/config.md |
| T037 | 7 | Blocked | SLA 0% 缺漏 2 分鐘內導入 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/SLAIngestionTest.kt |
| T038 | 7 | Blocked | 30 秒內回應與常用篩選使用情境 androidTest | android-app/app/src/androidTest/java/com/businesscard/app/FilterSLAViewTest.kt |
| T039 | 7 | Blocked | 報表完整性檢核 | automation/reports/completeness_check.sh |
| T040 | 7 | Blocked | 錯誤率 <2% 且 1 小時內修復報表/警示 | automation/reports/error_rate_alert.sh |
