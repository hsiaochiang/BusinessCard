# 語言治理與命名規範

## 一、目的
- 統一命名與用字規則，維持程式碼與文件一致性，降低審查與在地化成本。

## 二、命名規則（Domain／技術物件）
- Record／Entity：代表資料列（例：`ContactRecord`），Entity 為 Room 持久化（例：`ContactDraftEntity`）。
- Draft：暫存／離線資料（例：`ContactDraftForm`、`ContactDraftEntity`）。
- Query：查詢與過濾參數（例：`ContactQuery`）；SortBy／SyncStatus／Source 以列舉表示狀態或排序欄位。
- Repository：僅做資料來源協調（Local／Remote），不含 UI 邏輯；命名 `*Repository`。
- UseCase：動詞片語命名（例：`CreateContactUseCase`、`UpdateContactUseCase`），封裝單一路徑。
- Worker／Coordinator：背景任務與一致性調度（例：`UploadWorker`、`SyncCoordinator`）；避免承載業務決策。

## 三、檔名與資料夾結構
- Kotlin 檔名與類別同名且單一職責（例：`ContactRepository.kt` 管理聯絡人資料來源）。
- 測試：  
  - Unit／contract：`app/src/test/java/...`，以待測類別命名。  
  - 旅程／儀表：`app/src/androidTest/java/...`，以場景命名（例：`ScanCreateJourneyTest`、`SheetsQueryTest`）。
- 文件：`specs/001-business-card-manager/`、`docs/` 依主題分檔；腳本置於 `scripts/` 或 `tools/`。

## 四、字串與 i18n 原則
- 面向使用者的文案放在 `strings.xml`；測試或 log 用的說明可留在程式碼或測試資料。
- 避免硬編語言代碼；未來若多國語系，改用資源檔或字串表。
- 避免在程式碼中組接自然語言長句，改用格式化字串＋參數。
- Fake／測試資料可使用簡潔英／中文，不影響 UI。

## 五、審查清單（新增功能時檢核）
- 類別命名符合職責（Repository 不含 UI，UseCase 為動詞片語）。  
  - 字串是否置於適當資源檔；測試資料是否獨立於程式碼。  
  - 是否保留 Fake／Stub 注入點以利測試。  
  - 列舉／狀態命名是否遵循大寫或 UpperCamel（Source/SyncStatus/SortBy）。  
  - 檔案是否放在對應資料夾（main/test/androidTest/docs/scripts）。

## 六、待討論事項
- 錯誤代碼與訊息對應表、Telemetry 事件命名規範（待 US3 安全一致性階段補齊）。  
- 多國語系策略與審稿流程（待產品決策後訂定）。***
