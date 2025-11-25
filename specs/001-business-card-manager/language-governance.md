# 語言治理與命名規則

## 一、目的
- 確保程式碼與文件的命名、用字一致，便於後續維護、審查與在地化。

## 二、命名規則（Domain 與技術物件）
- Record/Entity：表示實際資料列（例：`ContactRecord`，`ContactDraftEntity`），Record 為 Domain，Entity 為 Room 持久化。
- Draft：暫存/離線資料（例：`ContactDraftForm`、`ContactDraftEntity`）。
- Query：封裝查詢與過濾參數（例：`ContactQuery`）。  
- Status/SyncStatus：狀態列舉（`PENDING/UPLOADING/FAILED/SYNCED`）。  
- SortBy：排序欄位列舉（`NAME/CREATED_AT`）。  
- Source：資料來源列舉（`SCAN/MANUAL/IMPORT`）。  
- Repository：僅負責資料來源協調（Local/Remote），不包含 UI 邏輯。  
- UseCase：動詞片語命名（例如 `CreateContactUseCase`、`UpdateContactUseCase`），封裝單一路徑的業務流程。  
- Worker：背景任務（例：`UploadWorker`），不做業務決策；Coordinator（例：`SyncCoordinator`）集中一致性邏輯。

## 三、檔名與資料夾結構
- Kotlin 檔名與類別同名，對應單一職責（例：`ContactRepository.kt` 只做資料協調）。  
- 測試：  
  - Unit/contract 測試：`app/src/test/java/...`，以待測類別為名。  
  - 旅程/儀表測試：`app/src/androidTest/java/...`，以場景命名（例：`ScanCreateJourneyTest`、`SheetsQueryTest`）。  
- 文件：`specs/001-business-card-manager/` 內依主題分檔（plan/tasks/architecture/language/performance）。  
- 腳本：`scripts/perf/` 放置量測腳本與說明。

## 四、字串與 i18n 原則
- 對使用者顯示的文案放在 `strings.xml`；測試資料或日誌中的說明性文字可留在程式碼。  
- 避免硬編語言代碼，未來多國語系時改用資源檔或字串表。  
- 避免在程式碼中動態拼接自然語言句子（以格式化字串 + 參數代入）。  
- 假服務/測試使用的字串可以為英文或簡短描述，不影響使用者介面。

## 五、待討論事項
- 錯誤代碼與訊息對應表、Telemetry 事件命名規則（待 US3 安全與一致性階段補齊）。  
- 是否引入多國語系（待產品決策後再定）。
