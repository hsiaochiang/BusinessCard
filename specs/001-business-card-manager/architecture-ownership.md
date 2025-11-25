# 架構與 Ownership 說明（US1 / US2 Phase1）

## 一、目的
- 提供新進成員與 Reviewer 快速理解 US1（掃描→暫存→離線上傳）與 US2 Phase1（Sheets 讀取 / 快取 / 搜尋 / 排序 / 軟刪除）的分層架構與責任邊界。

## 二、系統分層與資料流總覽
- UI / ViewModel 層：接收使用者操作（掃描、搜尋、排序、顯示列表），僅透過 UseCase 呼叫 Repository，不直接碰 Remote/DB。
- UseCase 層：
  - US1：`CreateContactUseCase` 建立暫存佇列並排程 WorkManager。
  - US2：`UpdateContactUseCase` 進行更新/軟刪除，`SyncCoordinator` 協調 last-write-wins 與資料同步。
- Repository 層：`ContactRepository` 協調 Local Room（暫存/快取）與 Remote（SheetsReader/SheetsWriter/DriveClient/OcrProcessor/Telemetry），封裝查詢、排序、過濾、同步邏輯。
- Local DB 層：Room Entity/Dao 儲存暫存與快取資料，提供查詢/狀態更新。
- Remote Client 層：Sheets/Drive/OCR/Telemetry 的介面與 stub/fake 實作，負責與外部服務或假服務互動。
- WorkManager：`UploadWorker` 處理離線佇列上傳，確保網路恢復後背景同步。
- 資料流（US1）：UI → `ScanViewModel` → `CreateContactUseCase` → `ContactRepository`（暫存佇列 + 圖片上傳 + Sheets 寫入）→ WorkManager 重試。
- 資料流（US2 Phase1）：UI 列表 → `ContactListViewModel` → `ContactRepository.observeContacts()`（快取 + 篩選排序）→ `refreshFromRemote()` 使用 `SheetsReader` 拉回分頁資料 → `SyncCoordinator`/UseCase 依 `updated_at` 做 last-write-wins。

## 三、主要模組與責任
- UI / Fragment / Activity：處理畫面與互動，僅觸發 ViewModel 事件；不直接存取資料層。
- ViewModel：管理 UI state，呼叫 UseCase/Repository，負責協調查詢條件、狀態呈現，不做資料轉換細節。
- UseCase：封裝單一路徑的業務操作（新增、更新、軟刪除、同步協調）；不處理 UI 細節。
- Repository：單一入口協調 Room 與 Remote，包含搜尋/排序/軟刪除/同步，避免 UI 直接組 SQL 或直接打 Remote。
- Local DB（Room）：儲存暫存與快取資料，提供 DAO 查詢與狀態更新；不包含業務判斷。
- Remote Client（SheetsClient/DriveClient/OcrProcessor/Telemetry + Fake/Stub）：封裝對外/假服務的呼叫與結果轉換；不持久化狀態。
- Worker / WorkManager：背景執行上傳佇列、重試；不承擔業務決策，只負責按佇列執行。

## 四、Ownership 與協作模式

| 模組/路徑 | 主要角色 | 審查責任 | 維運責任 |
|-----------|---------|----------|----------|
| android-app/app/src/main (UI/ViewModel/UseCase/Repository/Worker) | PG/SD | SD/SA | PG |
| android-app/app/src/androidTest (旅程/假服務測試) | QA/PG | QA/SD | QA |
| android-app/app/src/test (單元/契約測試) | QA/PG | QA/SD | QA |
| specs/001-business-card-manager/* (plan/spec/tasks/architecture 等文件) | SA | SA/PM | SA |
| scripts/perf/* (量測腳本) | QA/PG | QA/SD | QA |

## 五、未來擴充注意事項
- 新增 US2 後續 Phase 或 US3 時：
  - 仍透過 UseCase/Repository 取用資料，避免 UI 或 Worker 直連 Remote/DB。
  - 維持 Fake/Stub 注入能力，確保測試可離線運行。
  - 變更同步/一致性策略時，集中於 `SyncCoordinator` 與 Repository；不要把邏輯散落 UI/Worker。***
