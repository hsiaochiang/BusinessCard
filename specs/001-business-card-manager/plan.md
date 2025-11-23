# 實作計畫：名片管理系統第一版

**分支**：`001-business-card-manager` | **日期**：2025-11-22 | **規格**：specs/001-business-card-manager/spec.md  
**輸入**：來自 `/specs/001-business-card-manager/spec.md` 的功能規格

**語言要求**：本計畫與所有引用內容必須以繁體中文 (zh-TW) 撰寫，其他語言僅能作為附錄。  
**注意**：此模板由 `/speckit.plan` 指令產生；操作流程請參閱 `.specify/templates/commands/plan.md`。

## 摘要

- Android App 掃描紙本名片，採 Google ML Kit 裝置端 OCR 進行預填，並支援離線多筆暫存、恢復連線後逐筆自動上傳。  
- 新增/更新資料列時產生唯一 `id`（`yyyyMMddHHmmss` + 4 位隨機碼），同步 `source/created_at/updated_at`，影像上傳至指定 Google Drive 資料夾並寫入共享 URL。  
- Google 試算表 `Contacts` 為唯一資料來源，含欄位驗證、保護 `id` 欄位，桌面端可直接新增、修改、篩選、軟刪除（`is_deleted/deleted_at`），並維持最後寫入者覆蓋與稽核時間戳。  
- 安全與存取：Drive/試算表僅限受邀帳號存取，OAuth 授權可更換目標試算表/資料夾。  
- 體驗與效能：OCR 預填 5 秒內完成；試算表讀寫 2 秒內回應；列表載入需支援至少 5,000 筆資料。
- 發佈：僅產出簽署 APK 供 side-load 安裝，不涵蓋 Google Play 上架、審查、Play Integrity 或內容分級流程。

## 技術背景

**程式語言 / 版本**：Kotlin 1.9.x  
**主要相依**：Google ML Kit（裝置端文字辨識）、Google Sheets API、Google Drive API（以 Google Sign-In/OAuth 最小範圍存取受邀檔）  
**儲存層**：Google 試算表（主存）、Google Drive（影像）；本地離線暫存採 Room + 臨時影像檔  
**測試框架**：JUnit（單元/邏輯）、Espresso + Instrumentation（UI/整合）、Robolectric（離線/重試回歸），對 Sheets/Drive 使用 fake/mock 或錄製回放  
**目標平台**：Android API 29+（最低），目標/compile API 34  
**專案型態**：行動（Android App + 試算表/Drive 同步）  
**效能目標**：OCR 預填 ≤5 秒；試算表讀寫 ≤2 秒；列表 5,000 筆不逾時  
**運作限制**：需離線暫存多筆並自動補寫；權限僅限受邀帳號；需處理影像上傳失敗與 `id` 衝突提示；發佈採 side-load（簽署 APK），不經 Google Play 上架/審查  
**規模 / 範圍**：設計支援 5,000 筆為主，需容忍至 50,000 筆

## 憲章檢查

*閘門：Phase 0 研究前必須通過，Phase 1 設計後需再次確認。*

1. **程式碼品質監護**：需定義 Android 模組邊界與服務介面，建立 lint/靜態分析流程（如 ktlint/detekt）並在計畫內標註負責人；所有文件同步於 `/specs/001-business-card-manager/plan.md`。  
2. **測試界定真實**：為每個使用者故事撰寫至少一個整合/合約測試（掃描 + 寫入、試算表編輯 + 同步、同筆更新覆蓋），並目標達成 >=90% 陳述、>=75% 分支覆蓋；測試資料集與影像樣本需版控。  
3. **統一體驗保證**：遵循既有設計語言與無障礙要求（WCAG 2.1 AA），記錄主要螢幕的視覺回歸與在地化行為；使用共享元件並紀錄若有偏離。  
4. **效能可預測性**：明訂 OCR、同步、列表載入預算（見效能目標），規劃 RUM/遙測或 profiler 以監測；在計畫中標示監控指標與警戒門檻（≥5% 異常率告警），資料量上限 50k 需評估分頁與快取策略。  
5. **交付與流程標準**：文件（spec/plan/quickstart）全程 zh-TW，與程式碼變更同步更新；遙測計畫與 `/speckit.*` 產物需綁定憲章追蹤。  
6. **語言治理**：本計畫與相關文件皆以繁體中文撰寫，若有其他語言版本須標明來源並經審稿。

## 專案結構

### 文件（此功能）

```text
specs/001-business-card-manager/
├── plan.md              # 本檔案
├── research.md          # Phase 0 輸出
├── data-model.md        # Phase 1 輸出
├── quickstart.md        # Phase 1 輸出
├── contracts/           # Phase 1 輸出（API/流程合約）
└── tasks.md             # Phase 2 輸出
```

### 原始碼（版本庫根目錄）

```text
android/
└── app/
    ├── src/main/            # UI、掃描/OCR、同步模組
    ├── src/androidTest/     # 儀表/整合測試
    └── src/test/            # 單元/邏輯測試

automation/
└── sheets_drive/            # 試算表/Drive 佈建或回歸腳本（若需要）
```

**結構決策**：以 Android App 為主要程式碼庫，若需要自動化佈建/回歸腳本則置於 `automation/sheets_drive/`，目前不引入額外後端服務。

## 複雜度追蹤

> **僅在憲章檢查有違規但經核准時填寫**

| 違規項 | 為何需要 | 已拒絕的較簡方案 |
|--------|----------|------------------|
| （目前無） | （無） | （無） |
