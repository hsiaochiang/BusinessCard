# 架構與 Ownership 說明（US1／US2 Phase1）

## 一、目的
- 讓新進成員與 Reviewer 快速掌握 US1（掃描→暫存→離線上傳）與 US2 Phase1（Sheets 讀取／快取／搜尋／排序／軟刪除）的分層架構、資料流與責任邊界。

## 二、系統分層與資料流總覽
- UI／ViewModel：接收使用者操作（掃描、搜尋、排序、顯示列表），僅呼叫 UseCase／Repository；不直接碰 Remote 或 DB。
- UseCase：
  - US1：`CreateContactUseCase` 建立暫存佇列並排程 WorkManager。
  - US2：`UpdateContactUseCase` 進行更新／軟刪除，`SyncCoordinator` 集中 last-write-wins 與資料合併策略。
- Repository：`ContactRepository` 協調 Local（Room）與 Remote（SheetsReader／SheetsWriter／DriveClient／OcrProcessor／Telemetry），封裝搜尋、排序、過濾、同步邏輯。
- Local DB：Room Entity／Dao 儲存暫存與快取資料，提供查詢與狀態更新；不包含業務判斷。
- Remote Client：Sheets／Drive／OCR／Telemetry 介面及 stub/fake；負責對外呼叫或假服務互動，不持久化狀態。
- WorkManager：`UploadWorker` 處理離線佇列上傳與重試；不做業務決策，只執行佇列。
- 資料流（US1）：UI → `ScanViewModel` → `CreateContactUseCase` → `ContactRepository`（暫存＋影像上傳＋ Sheets 寫入）→ WorkManager 重試。
- 資料流（US2 Phase1）：UI 列表 → `ContactListViewModel` → `ContactRepository.observeContacts()`（快取＋篩選排序）→ `refreshFromRemote()` 使用 `SheetsReader` 拉回分頁資料 → `SyncCoordinator` 依 `updated_at` 決定覆蓋方向。

## 三、主要模組與責任
- UI／Fragment／Activity：處理畫面與互動，觸發 ViewModel 事件；不直接存取資料層。
- ViewModel：管理 UI state，呼叫 UseCase／Repository，協調查詢條件與狀態呈現；不進行資料層決策。
- UseCase：封裝單一路徑的業務操作（新增、更新、軟刪除、同步協調）；不處理 UI 細節。
- Repository：單一入口協調 Room 與 Remote，包含搜尋／排序／軟刪除／同步；避免 UI 直接組 SQL 或直連 Remote。
- Local DB（Room）：儲存暫存與快取資料，提供 DAO 查詢與狀態更新；不含業務邏輯。
- Remote Client（SheetsClient／DriveClient／OcrProcessor／Telemetry + Fake/Stub）：封裝對外／假服務呼叫與結果轉換；不持久化狀態。
- Worker／WorkManager：背景執行佇列與重試；不承擔業務決策。

## 四、Ownership 與協作模式

| 模組／路徑 | 主要角色 | 審查責任 | 維運責任 |
|-----------|---------|----------|----------|
| android-app/app/src/main (UI／ViewModel／UseCase／Repository／Worker) | PG／SD | SD／SA | PG |
| android-app/app/src/androidTest (旅程／假服務測試) | QA／PG | QA／SD | QA |
| android-app/app/src/test (單元／契約測試) | QA／PG | QA／SD | QA |
| specs/001-business-card-manager/* | SA | SA／PM | SA |
| docs/* | SA／SD | SA | SA |
| scripts/perf/* | QA／PG | QA／SD | QA |

## 五、未來擴充注意事項
- 新增 US2 後續 Phase 或 US3 時，仍需透過 UseCase／Repository，避免 UI 或 Worker 直連資料層。
- 維持 Fake／Stub 注入能力，確保離線與自動化測試可運行。
- 同步與一致性策略集中在 `SyncCoordinator` 與 Repository，避免邏輯分散於 UI／Worker。***
