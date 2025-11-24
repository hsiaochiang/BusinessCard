# 任務：名片管理系統第一版（side-load APK）

**輸入**：`spec.md`、`plan.md`、`research.md`、`data-model.md`、`contracts/`  
**策略**：本階段僅整理規格與任務，暫不建立 Android 原始碼；所有 Android 相關工作維持 Blocked，待後續建立 `android-app/` 後再展開。  
**憑證與設定**：以環境變數/placeholder 描述，不提交實體檔案（例如 `GOOGLE_SERVICES_JSON_PATH`、`ANDROID_RELEASE_KEYSTORE_PATH`、`OAUTH_CLIENT_CONFIG_PATH`）。

目前所有工作皆待 `android-app/` 專案骨架建立後才能啟動；後續若有可執行任務會另行分組。

## Blocked 任務總表（集中追蹤）

| 任務 | Phase | 狀態 | 重點描述 | 路徑/檔案/設定 |
|------|-------|------|----------|----------------|
| T001 | 1 | Blocked | 建立 `android-app/` Kotlin/Gradle 專案骨架與目錄（app 模組、Gradle wrapper、.gitignore、Room/CI 插槽） | android-app/, android-app/app/build.gradle.kts |
| T002 | 1 | Blocked [P] | 佈建 lint/測試/CI 模板（ktlint/detekt、覆蓋率門檻、CI workflow 草稿），不觸發實際建置 | .github/workflows/android-ci.yml |
| T003 | 1 | Blocked | 設定簽署與雲端服務的 placeholder：`ANDROID_RELEASE_KEYSTORE_PATH`、`GOOGLE_SERVICES_JSON_PATH`、`OAUTH_CLIENT_CONFIG_PATH`，由環境變數/密鑰管理提供 | Gradle signingConfig、雲端設定環境變數 |
| T004 | 2 | Blocked | 定義資料層與同步骨架（Room schema、同步佇列、重試/錯誤介面），確認與 data-model 一致 | android-app/app/src/main/java/com/businesscard/app/data/, docs |
| T005 | 2 | Blocked [P] | Sheets/Drive 封裝與授權流程設計：以 OAuth 範圍、試算表/資料夾 ID placeholder，並規劃欄位/權限佈建腳本 | automation/sheets_drive/, env: SHEETS_SPREADSHEET_ID, DRIVE_FOLDER_ID |
| T006 | 2 | Blocked [P] | 日誌、遙測與警示策略（事件命名、性能門檻、告警匯報介面），預留 5k/50k 資料量的監控點 | android-app/app/src/main/java/com/businesscard/app/telemetry |
| T007 | 3 | Blocked | US1：掃描/ML Kit OCR 流程與 UI 架構，涵蓋權限、拍照預覽、欄位預填、錯誤提示 | android-app/app/src/main/java/com/businesscard/app/ui/scan |
| T008 | 3 | Blocked [P] | US1：寫入與同步流程設計（id 生成、Room 暫存、入佇列、Drive 上傳與 URL 回寫），含失敗重試策略 | android-app/app/src/main/java/com/businesscard/app/domain/create, .../data/remote/drive |
| T009 | 3 | Blocked | US1：整合/儀表測試計畫（OCR + 同步 + 上傳重試），含 androidTest 範本與測試資料準備 | android-app/app/src/androidTest/java/com/businesscard/app/ |
| T010 | 4 | Blocked | US2：查詢/篩選/排序與快取策略，支援 5k~50k 列，預設排除 `is_deleted=true` | android-app/app/src/main/java/com/businesscard/app/data/remote/sheets/Queries.kt |
| T011 | 4 | Blocked [P] | US2：更新/刪除/衝突解決政策（桌面端 vs App），同步與回復流程設計 | android-app/app/src/main/java/com/businesscard/app/domain/update, .../sync |
| T012 | 5 | Blocked | US3：OAuth/帳號切換/權限檢查流程，限制受邀帳號並記錄稽核資訊 | android-app/app/src/main/java/com/businesscard/app/auth |
| T013 | 5 | Blocked [P] | 安全與合規強化（權杖存取最小化、日誌掩碼、敏感欄位控管）、權限一致性測試計畫 | android-app/app/src/main/java/com/businesscard/app/security |
| T014 | 6 | Blocked | 打包/簽署/side-load 發佈流程（Gradle 任務、簽章參數從環境變數讀取）、手動安裝/更新檢核腳本 | android-app/app/build.gradle.kts, tests/manual/sideload.md |
| T015 | 6 | Blocked [P] | 發佈後迴歸/性能/容量/警示報表（50k 資料、錯誤率 <2%）的測試與自動化規劃 | automation/ci/, automation/reports/ |
| T016 | 7 | Blocked | 文件/流程同步更新：spec/plan/quickstart/tasks、佈建腳本與報表說明 | docs/, specs/001-business-card-manager/, automation/ |
