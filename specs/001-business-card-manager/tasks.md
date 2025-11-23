# 任務：名片管理系統第一版（side-load APK）

**輸入**：`spec.md`、`plan.md`、`research.md`、`data-model.md`、`contracts/`  
**前置需求**：依計畫完成各階段；發佈僅產出簽署 APK 供 side-load，不涉 Google Play 上架。

## Phase 1：初始化
- [ ] T001 建立/確認 Android Gradle 專案設定與版本（android/app/build.gradle.kts）
- [ ] T002 [P] 設定 detekt/ktlint 與 CI 腳本（android/app/，.github/workflows/ 如有）
- [ ] T003 設定簽章與 keystore 參數（android/app/keystore.properties）
- [ ] T004 [P] 加入 google-services 與 OAuth Client 設定（android/app/google-services.json）

## Phase 2：基礎建設（阻擋性）
- [ ] T005 建立 Room 基礎與 ContactRecord/ContactImage schema（android/app/src/main/java/com/businesscard/app/data/db）
- [ ] T006 建立 Sheets/Drive API 封裝與授權流程（android/app/src/main/java/com/businesscard/app/data/remote）
- [ ] T007 [P] 實作統一日誌與錯誤處理、脫敏（android/app/src/main/java/com/businesscard/app/core/logging）
- [ ] T008 建立離線佇列/重試機制骨架（android/app/src/main/java/com/businesscard/app/sync）
- [ ] T009 [P] 設定遙測/指標收集介面（android/app/src/main/java/com/businesscard/app/telemetry）
- [ ] T010 佈建 Contacts 試算表欄位/驗證/表頭與 `id` 欄保護並回歸檢查（automation/sheets_drive/contacts_schema_setup.sh）
- [ ] T011 檢查並設定 Drive/Sheets 權限：僅受邀帳號、禁止公開連結（automation/sheets_drive/permissions_check.sh）
- [ ] T012 CI 覆蓋率守門：>=90% 陳述 / >=75% 分支，失敗阻擋（.github/workflows/android-ci.yml）
- [ ] T013 效能基線量測與報表：OCR ≤5 秒、Sheets 讀寫 ≤2 秒、列表 5k；異常率 ≥5% 告警（android/app/src/main/java/com/businesscard/app/perf/PerformanceMonitor.kt + CI 報表）

## Phase 3：使用者故事 1 - 手機掃描並建檔（P1）
- [ ] T014 [US1] 建立掃描與 ML Kit OCR 預填 UI（android/app/src/main/java/com/businesscard/app/ui/scan）
- [ ] T015 [P] [US1] 建立名片建立流程：生成 `id`、寫入 Room 暫存、佇列同步（android/app/src/main/java/com/businesscard/app/domain/create）
- [ ] T016 [P] [US1] 影像上傳與 URL 回寫（android/app/src/main/java/com/businesscard/app/data/remote/drive）
- [ ] T017 [US1] 契約/整合測試：掃描→儲存→寫入試算表（android/app/src/androidTest/java/com/businesscard/app/ScanSyncTest.kt）
- [ ] T018 [US1] 影像上傳失敗重試與待補標記實作（android/app/src/main/java/com/businesscard/app/data/remote/drive/UploadRetrier.kt）
- [ ] T019 [US1] androidTest 驗證上傳失敗保留資料列並提示重傳（android/app/src/androidTest/java/com/businesscard/app/UploadRetryTest.kt）

## Phase 4：使用者故事 2 - 桌面端編修與查詢（P1）
- [ ] T020 [US2] 實作資料讀取/篩選/排序（android/app/src/main/java/com/businesscard/app/data/remote/sheets/Queries.kt）
- [ ] T021 [P] [US2] 更新/軟刪除寫回流程，刷新 `updated_at`、`is_deleted`（android/app/src/main/java/com/businesscard/app/domain/update）
- [ ] T022 [US2] 整合測試：桌面更新後 App 重新整理顯示最新列（android/app/src/androidTest/java/com/businesscard/app/SheetUpdateSyncTest.kt）
- [ ] T023 [US2] 處理試算表端 `id` 衝突提示與回復策略（android/app/src/main/java/com/businesscard/app/sync/ConflictResolver.kt）
- [ ] T024 [US2] androidTest 驗證桌面插入/衝突情境與提示（android/app/src/androidTest/java/com/businesscard/app/SheetConflictTest.kt）

## Phase 5：使用者故事 3 - 帳號安全與同步一致性（P2）
- [ ] T025 [US3] OAuth 設定/切換試算表與資料夾，受邀帳號檢查（android/app/src/main/java/com/businesscard/app/auth）
- [ ] T026 [P] [US3] 衝突處理：最後寫入者覆蓋、`updated_at` 校驗（android/app/src/main/java/com/businesscard/app/sync/conflict)
- [ ] T027 [US3] 撤銷授權/重新登入與一致性驗證測試（android/app/src/androidTest/java/com/businesscard/app/AuthConsistencyTest.kt）
- [ ] T028 [US3] 測試未受邀帳號/公開連結存取被拒（android/app/src/androidTest/java/com/businesscard/app/AccessControlTest.kt）

## Phase 6：發佈與 side-load（不上架 Play）
- [ ] T029 產出簽署 APK：`./gradlew assembleRelease`（android/app/build.gradle.kts）
- [ ] T030 [P] 驗證 side-load 安裝與啟動/授權流程並記錄結果：`adb install -r app/build/outputs/apk/release/app-release.apk`（tests/manual/sideload.md）
- [ ] T031 回歸測試：以簽署 APK 執行整合/契約/離線重試（android/app/src/androidTest/java/com/businesscard/app/ReleaseRegressionTest.kt）

## Phase 7：潤飾與跨故事
- [ ] T032 清理程式碼/文件並同步 spec/plan/quickstart（docs/ 或 specs/001-business-card-manager/）
- [ ] T033 [P] 追加效能/遙測優化（分頁/快取）並抽樣 50k 列測試，對齊效能預算（android/app/src/main/java/com/businesscard/app/perf）
- [ ] T034 安全性強化：權限範圍、token 管理、日誌脫敏檢查（android/app/src/main/java/com/businesscard/app/security）
- [ ] T035 排程每日回歸（整合/契約/離線重試）並產出報表（automation/ci/daily-regression.sh）
- [ ] T036 遙測異常（≥5%）告警設定與驗證（android/app/src/main/java/com/businesscard/app/telemetry/AlertingConfig.kt + automation/alerts/config.md）
- [ ] T037 合成測試：80% 名片 2 分鐘內入表（android/app/src/androidTest/java/com/businesscard/app/SLAIngestionTest.kt）
- [ ] T038 合成測試：篩選視圖 30 秒內取得名單，含常用視圖使用率檢核（android/app/src/androidTest/java/com/businesscard/app/FilterSLAViewTest.kt）
- [ ] T039 報表檢核：欄位完整率 ≥95%（automation/reports/completeness_check.sh）
- [ ] T040 錯誤率 <2% 與 1 日內追蹤修復報表/告警（automation/reports/error_rate_alert.sh）

## 依賴與執行順序
- 故事優先順序：US1 (P1) → US2 (P1) → US3 (P2)。
- Phase 1 → Phase 2 為阻擋前置；完成後可平行 US1/US2；US3 在核心同步穩定後進行。

## 平行作業範例
- Phase 1：T002（lint）可與 T003（簽章）並行。
- Phase 2：T006（API 封裝）與 T007（logging）可並行，需在 T008 佇列骨架前完成基礎介面。
- US1：T011（建立流程）與 T012（影像上傳）可並行，T013 測試需等待兩者完成。
- US2：T014 資料讀取與 T015 更新寫回可並行，T016 測試在兩者完成後。
- 發佈：T021 side-load 驗證可與 T022 回歸測試並行（使用簽署 APK）。

## 實作策略
- MVP：完成 Phase 1–2，再交付 US1（掃描→寫入 Sheets），驗證 side-load APK。
- 漸進式：在 US1 穩定後並行 US2，最後處理 US3 的權限與衝突，收斂於 Phase 7 潤飾。
