# Research - 名片管理系統第一版

**日期**：2025-11-22  
**來源**：功能規格 `specs/001-business-card-manager/spec.md`，計畫 `plan.md`

## 決策與依據

### Kotlin / Android 版本
- **Decision**：Kotlin 1.9.x；Android 最低支援 API 29（Android 10），目標/compile API 34。
- **Rationale**：API 29 確保現代權限/檔案存取模型並兼容多數現役裝置；API 34 為最新 LTS，便於安全修補與商店要求。
- **Alternatives considered**：更低版本（<29，放寬存取規則但增加安全/相容風險）；僅鎖定 API 33（較短維運周期）。

### Sheets/Drive 授權與使用方式
- **Decision**：採 Google Sign-In/OAuth 2，請求最小必要範圍（Sheets/Drive 受邀檔案存取），權杖交由 Google Play services 管理；App 內可切換目標試算表/資料夾。
- **Rationale**：符合最小權限原則與企業受邀帳號限制，減少自行儲存 refresh token 的安全風險。
- **Alternatives considered**：服務帳號（不符終端用戶授權流程且較難限制個人受邀帳號）；長期本地儲存 access/refresh token（安全風險高）。

### 發佈/交付方式
- **Decision**：僅產出簽署 APK，透過 side-load 分發與安裝，不進行 Google Play 上架、審查或 Play Integrity 整合。
- **Rationale**：避免商店審查/內容分級流程，符合內部分發需求；保留必要安全/隱私控制。
- **Alternatives considered**：上架 Google Play（需要額外審查與 Play Integrity）；企業內部分發管道（需額外 MDM/託管成本）。

### 離線暫存方案
- **Decision**：使用 Room 本地資料庫儲存暫存名片與上傳佇列；影像離線儲存為臨時檔路徑並與暫存記錄關聯。
- **Rationale**：Room 提供交易/佇列與 schema 管理，易於重試與觀察狀態；比純記憶體或檔案更安全可靠。
- **Alternatives considered**：僅檔案+JSON（缺少交易與一致性保障）；純記憶體（無持久性）；自行 SQLite 實作（維運成本較高）。

### 測試框架與端對端策略
- **Decision**：單元/邏輯測試採 JUnit；UI/整合使用 Espresso + Instrumentation；離線/重試邏輯可用 Robolectric 作快速回歸；Sheets/Drive 互動以 fake/mock service 或錄製回放。
- **Rationale**：符合 Android 常見工具鏈，兼顧真機/模擬器驗證與快速本地回歸。
- **Alternatives considered**：僅使用 Instrumentation（回歸速度慢）；僅 Robolectric（缺真機行為）；Cucumber/BDD（增加維護成本）。

### 目標資料量
- **Decision**：主要預期 5,000 列，設計上需容忍至 50,000 列（查詢/篩選與分頁策略需可支援）。
- **Rationale**：規格要求 5,000 列不逾時；預留量級以避免近期升級成本。
- **Alternatives considered**：只鎖定 5,000（短期足夠但缺擴充彈性）；>100,000（超出試算表實務效能，需改用資料庫）。 
