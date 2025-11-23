# 任務：名片管理系統第一版（side-load APK）

**輸入**：`spec.md`、`plan.md`、`research.md`、`data-model.md`、`contracts/`  
**前置需求**：依計畫完成各階段；發佈僅產出簽署 APK 供 side-load，不涉 Google Play 上架。

## Phase 1：初始化
- [ ] T001 建立/確認 Android Gradle 專案設定與版本（android/app/build.gradle.kts）
- [ ] T002 [P] 設定 detekt/ktlint 與 CI 腳本（android/app/，.github/workflows/ 如有）
- [ ] T003 設定簽章與 keystore 參數（android/app/keystore.properties）
- [ ] T004 [P] 加入 google-services 與 OAuth Client 設定（android/app/google-services.json）

## Phase 2：基礎建設（阻擋性）
- [ ] T005 建立 Room 基礎與 ContactRecord/ContactImage schema（android/app/src/main/java/.../data/db）
- [ ] T006 建立 Sheets/Drive API 封裝與授權流程（android/app/src/main/java/.../data/remote）
- [ ] T007 [P] 實作統一日誌與錯誤處理、脫敏（android/app/src/main/java/.../core/logging）
- [ ] T008 建立離線佇列/重試機制骨架（android/app/src/main/java/.../sync）
- [ ] T009 [P] 設定遙測/指標收集介面（android/app/src/main/java/.../telemetry）

## Phase 3：使用者故事 1 - 手機掃描並建檔（P1）
- [ ] T010 [US1] 建立掃描與 ML Kit OCR 預填 UI（android/app/src/main/java/.../ui/scan）
- [ ] T011 [P] [US1] 建立名片建立流程：生成 `id`、寫入 Room 暫存、佇列同步（android/app/src/main/java/.../domain/create）
- [ ] T012 [P] [US1] 影像上傳與 URL 回寫（android/app/src/main/java/.../data/remote/drive）
- [ ] T013 [US1] 契約/整合測試：掃描→儲存→寫入試算表（android/app/src/androidTest/java/.../ScanSyncTest.kt）

## Phase 4：使用者故事 2 - 桌面端編修與查詢（P1）
- [ ] T014 [US2] 實作資料讀取/篩選/排序（android/app/src/main/java/.../data/remote/sheets/Queries.kt）
- [ ] T015 [P] [US2] 更新/軟刪除寫回流程，刷新 `updated_at`、`is_deleted`（android/app/src/main/java/.../domain/update）
- [ ] T016 [US2] 整合測試：桌面更新後 App 重新整理顯示最新列（android/app/src/androidTest/java/.../SheetUpdateSyncTest.kt）

## Phase 5：使用者故事 3 - 帳號安全與同步一致性（P2）
- [ ] T017 [US3] OAuth 設定/切換試算表與資料夾，受邀帳號檢查（android/app/src/main/java/.../auth）
- [ ] T018 [P] [US3] 衝突處理：最後寫入者覆蓋、`updated_at` 校驗（android/app/src/main/java/.../sync/conflict)
- [ ] T019 [US3] 撤銷授權/重新登入與一致性驗證測試（android/app/src/androidTest/java/.../AuthConsistencyTest.kt）

## Phase 6：發佈與 side-load（不上架 Play）
- [ ] T020 產出簽署 APK：`./gradlew assembleRelease`（android/app/build.gradle.kts）
- [ ] T021 [P] 驗證 side-load 安裝與啟動流程：`adb install -r app/build/outputs/apk/release/app-release.apk`（tests/manual/sideload.md）
- [ ] T022 回歸測試：以簽署 APK 執行整合/契約/離線重試（android/app/src/androidTest/java/.../ReleaseRegressionTest.kt）

## Phase 7：潤飾與跨故事
- [ ] T023 清理程式碼/文件並同步 spec/plan/quickstart（docs/ 或 specs/001-business-card-manager/）
- [ ] T024 [P] 追加效能/遙測優化（分頁/快取）並抽樣 50k 列測試（android/app/src/main/java/.../perf）
- [ ] T025 安全性強化：權限範圍、token 管理、日誌脫敏檢查（android/app/src/main/java/.../security）

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
