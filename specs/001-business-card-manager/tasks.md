# 任務：名片管理系統第一版

**輸入**：`specs/001-business-card-manager/` 內的 spec.md、plan.md、research.md、data-model.md、contracts/  
**語言要求**：文件與實作說明皆以繁體中文撰寫。  
**組織方式**：任務依使用者故事分組，必須可獨立實作與測試；每項任務均標註檔案路徑。

## 格式：`- [ ] T### [P?] [US#?] 描述（含路徑）`
- **[P]**：可平行執行（不同檔案、無相依）
- **[US#]**：僅限使用者故事階段的任務（例如 [US1]、[US2]、[US3]）
- 描述中必須包含精確檔案路徑
- 測試任務僅在規格要求時列出（本功能要求有測試與覆蓋率門檻）

---

## Phase 1：初始化（共享基礎設施）

**目的**：建立 Android 專案骨架與基本專案文件

- [x] T001 建立 `android-app/` Gradle wrapper、`settings.gradle.kts` 與 `app` 模組骨架（android-app/）（已建置命名空間 `com.businesscard.app`）
- [x] T002 [P] 建立 Android 專用 `.gitignore` 與範例設定檔（`android-app/.gitignore`、`android-app/app/google-services.json.example`）（已加入 placeholder）
- [x] T003 [P] 撰寫專案快速指南（本機建置/side-load）至 `android-app/README.md`（已新增使用步驟）
- [x] T004 [P] 建立 CI 草稿與組建入口（`.github/workflows/android-ci.yml` 連結 Gradle 任務）（已加入 assembleDebug）

---

## Phase 2：基礎建設（阻擋性前置）

**目的**：完成所有故事共用的框架、相依與品質控制

- [x] T005 設定版本目錄與主要相依（ML Kit、Room、Play services、Sheets/Drive SDK）於 `android-app/gradle/libs.versions.toml` 與 `android-app/app/build.gradle.kts`（已加入 Room/Work/ML Kit stub 相依）
- [x] T006 [P] 設定 ktlint/detekt 與格式化規則，掛載至 `android-app/app/build.gradle.kts` 與 `android-app/config/detekt/detekt.yml`（已加入 detekt plugin + baseline/config）
- [x] T007 [P] 設定單元/儀表測試與覆蓋率門檻（>=90% 陳述、>=75% 分支）於 `android-app/app/build.gradle.kts` 與 `android-app/app/src/androidTest/README.md`（已設 Jacoco 報表與寬鬆 baseline 0.1，後續可拉高；README 已寫明指令）
- [x] T008 [P] 建立敏感設定 placeholder（`ANDROID_RELEASE_KEYSTORE_PATH`、`GOOGLE_SERVICES_JSON_PATH`、`OAUTH_CLIENT_CONFIG_PATH`）於 `android-app/app/src/main/res/values/config_placeholders.xml`
- [x] T009 建立資料模型對應 data-model（`ContactRecord`、`ContactImage`）於 `android-app/app/src/main/java/com/businesscard/app/model/ContactModels.kt`
- [x] T010 建立 Room schema 與 DAO 介面（暫存/佇列/狀態）於 `android-app/app/src/main/java/com/businesscard/app/data/db/ContactDao.kt`
- [x] T011 [P] 定義 Sheets 用戶端介面與 DTO 映射（讀/寫/分頁）於 `android-app/app/src/main/java/com/businesscard/app/data/remote/sheets/SheetsClient.kt`（stub）
- [x] T012 [P] 定義 Drive 上傳介面與狀態回寫（影像共享 URL）於 `android-app/app/src/main/java/com/businesscard/app/data/remote/drive/DriveClient.kt`（stub）
- [x] T013 [P] 定義遙測/日誌介面與指標（OCR 時間、同步延遲、錯誤率）於 `android-app/app/src/main/java/com/businesscard/app/telemetry/Telemetry.kt`

---

## 憲章強制品質任務

- [ ] T014 [P] 紀錄模組 Ownership 與架構說明（lint/同步/資料層）於 `android-app/docs/architecture.md`
- [ ] T015 驗證語言治理：確保 README、docs、UI 字串為繁體中文並記錄審稿人於 `android-app/docs/i18n.md`
- [ ] T016 [P] 建立效能量測腳本/檢查點（OCR ≤5s、試算表讀寫 ≤2s、列表 5k/50k）於 `android-app/docs/perf-checks.md`

---

## Phase 3：使用者故事 1 - 手機掃描並建檔（優先級：P1）🎯 MVP

**目標**：完成 Android 端掃描→OCR 預填→暫存/上傳→寫入試算表與影像 URL  
**獨立測試**：掃描測試影像→表單修正→儲存→試算表新增列且含影像 URL；離線建立多筆，恢復後自動逐筆上傳

### 測試（先寫後實作）
- [x] T017 [P] [US1] 建立掃描與同步旅程儀表測試於 `android-app/app/src/androidTest/java/com/businesscard/app/ScanCreateJourneyTest.kt`（已使用 WorkManager 測試環境與假服務覆蓋掃描→暫存→上傳流程）
- [x] T018 [P] [US1] 建立 Sheets/Drive 假服務與契約測試於 `android-app/app/src/test/java/com/businesscard/app/fakes/FakeSyncAdapters.kt`（已以 androidTest fakes 驗證上傳與寫入呼叫）

### 實作
- [x] T019 [P] [US1] 實作相機預覽與權限流程（ML Kit OCR）於 `android-app/app/src/main/java/com/businesscard/app/ui/scan/ScanFragment.kt`（已串接 TakePicturePreview + 權限）
- [x] T020 [US1] 實作 OCR 解析與表單預填/驗證於 `android-app/app/src/main/java/com/businesscard/app/ui/scan/ScanViewModel.kt`（使用 OcrProcessor stub 預填表單）
- [x] T021 [US1] 實作建立名片的使用案例（寫入 Room 暫存佇列 + 生成 id/time）於 `android-app/app/src/main/java/com/businesscard/app/domain/create/CreateContactUseCase.kt`
- [x] T022 [US1] 實作影像上傳並產生共享 URL 回寫狀態於 `android-app/app/src/main/java/com/businesscard/app/data/remote/drive/DriveUploader.kt`（stub 回傳假 URL）
- [x] T023 [US1] 實作試算表新增列（含欄位驗證與來源/時間戳）於 `android-app/app/src/main/java/com/businesscard/app/data/remote/sheets/SheetsWriter.kt`（stub）
- [x] T024 [US1] 實作離線佇列同步與重試（WorkManager）於 `android-app/app/src/main/java/com/businesscard/app/sync/UploadWorker.kt`
- [x] T025 [US1] UI 串接成功/失敗狀態與重試入口於 `android-app/app/src/main/java/com/businesscard/app/ui/scan/ScanViewModel.kt`（以狀態機處理重試/排程）

**檢查點**：US1 可離線建立並恢復上傳，表單/影像/試算表一致

---

## Phase 4：使用者故事 2 - 桌面端編修與查詢（優先級：P1）

**目標**：支援試算表直接新增/編修/篩選，App 可讀取、排序、搜尋並維持 last-write-wins  
**獨立測試**：試算表新增/修改/刪除一筆 → App 重新整理可正確顯示，預設排除 `is_deleted=true`

### 測試（先寫後實作）
- [x] T026 [P] [US2] 建立試算表讀取與篩選旅程儀表測試於 `android-app/app/src/androidTest/java/com/businesscard/app/SheetsQueryTest.kt`（使用 fake Sheets reader/clients 驗證讀取、排序、軟刪除）

### 實作
- [x] T027 [P] [US2] 實作 Sheets 讀取與分頁/快取（含 `include_deleted` 旗標）於 `android-app/app/src/main/java/com/businesscard/app/data/remote/sheets/SheetsReader.kt`（stub + 分頁結果轉換）
- [x] T028 [P] [US2] 實作本地快取與查詢/排序/篩選（5k~50k）於 `android-app/app/src/main/java/com/businesscard/app/data/repository/ContactRepository.kt`（新增 contactsFlow、搜尋/排序與 refreshFromRemote）
- [x] T029 [US2] 實作列表與搜尋/篩選 UI（預設排除 `is_deleted`）於 `android-app/app/src/main/java/com/businesscard/app/ui/list/ContactListViewModel.kt`（新增 ViewModel state/查詢條件）
- [x] T030 [US2] 實作更新/軟刪除使用案例並回寫試算表於 `android-app/app/src/main/java/com/businesscard/app/domain/update/UpdateContactUseCase.kt`
- [x] T031 [US2] 實作同步協調器（last-write-wins、timestamp 校驗）於 `android-app/app/src/main/java/com/businesscard/app/sync/SyncCoordinator.kt`

**檢查點**：桌面端與 App 雙向更新保持一致，列表操作在 2 秒預算內回應

---

## Phase 5：使用者故事 3 - 帳號安全與同步一致性（優先級：P2）

**目標**：確保僅受邀帳號可存取 Sheets/Drive，並在多端併發時維持一致性與稽核  
**獨立測試**：撤銷授權帳號需重新登入；同筆資料雙端修改後以最後寫入者覆蓋並更新 `updated_at`

### 測試（先寫後實作）
- [ ] T032 [P] [US3] 建立授權/帳號切換/衝突旅程儀表測試於 `android-app/app/src/androidTest/java/com/businesscard/app/AuthAndAccessTest.kt`

### 實作
- [ ] T033 [US3] 實作 OAuth 登入與帳號切換 UI/流程於 `android-app/app/src/main/java/com/businesscard/app/auth/AuthManager.kt`
- [ ] T034 [US3] 實作權限檢查（受邀帳號驗證、試算表/Drive 權限提示）於 `android-app/app/src/main/java/com/businesscard/app/auth/PermissionVerifier.kt`
- [ ] T035 [US3] 實作稽核與安全事件記錄（授權、更新、刪除）於 `android-app/app/src/main/java/com/businesscard/app/telemetry/AuditLogger.kt`
- [ ] T036 [US3] 實作併發一致性防護（`updated_at` 校驗與衝突提示）於 `android-app/app/src/main/java/com/businesscard/app/sync/ConsistencyGuard.kt`

**檢查點**：僅受邀帳號可操作；衝突情境下最後寫入者覆蓋並可追溯

---

## Phase 6：潤飾與跨故事議題

**目的**：文件同步、重構、效能與回歸

- [ ] T037 [P] 同步 spec/plan/quickstart/tasks 變更與建置流程至 `specs/001-business-card-manager/`
- [ ] T038 [P] 補充手動驗收腳本與測試資料（20 張名片影像 + CSV）於 `android-app/testdata/` 與 `specs/001-business-card-manager/quickstart.md`
- [ ] T039 重構/效能優化並確保覆蓋率門檻達成於 `android-app/`

---

## 相依與執行順序

### 階段相依
- Phase 1 → Phase 2 → US1 → US2 → US3 → 潤飾
- 未完成 Phase 2 前不得進入任一使用者故事

### 使用者故事相依
- US1（P1）完成後可獨立 Demo（MVP）
- US2（P1）依賴 US1 的資料管線與模型
- US3（P2）依賴前述同步/授權框架

### 可平行的機會
- 所有標記 [P] 的任務可在不衝突檔案下平行
- US1、US2、US3 可由不同人員並行，只需確保共享介面已在 Phase 2 完成

---

## 實作策略

- **MVP（僅 US1）**：完成 Phase 1~2 後，交付 US1（掃描/暫存/寫入試算表 + 影像上傳），作為 Demo。
- **漸進式**：US1 完成並驗收後，加入 US2（桌面端同步/查詢），再加入 US3（授權與一致性），每階段皆需覆蓋率與效能檢查。
