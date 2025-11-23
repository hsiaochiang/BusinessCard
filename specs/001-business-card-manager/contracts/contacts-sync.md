# Contracts - Contacts Sync (名片管理系統第一版)

**目的**：定義 App 與後端（Google Sheets/Drive + 同步服務層）的互動契約，確保掃描、同步、更新與軟刪除行為可測試。  
**格式**：REST 風格介面，作為本地同步層/自動化腳本的合約參考；實作可直接呼叫 Google API 或以代理服務封裝。

## 欄位約定
- `id`：`yyyyMMddHHmmss` + 4 位隨機碼（App 或試算表腳本生成，唯一）。
- `source`：`scan` | `manual` | `import`。
- `is_deleted`：布林，預設 false；軟刪除時設定 `true` 並填寫 `deleted_at`。
- 時間：ISO 8601（UTC+08:00，台北時區）。

## Endpoints（邏輯合約）

### 1) 建立名片
- **POST** `/contacts`
- **Request body**：
  - `id` (string, required)
  - `name` (string, required)
  - `company`, `title`, `email`, `phone`, `tags`, `notes` (optional)
  - `source` (enum, required)
  - `image_upload` (binary or pre-signed URL upload step)
- **Response**：`201 Created` with contact payload + `created_at`, `updated_at`, `image_url`（若先上傳成功）。
- **試算表映射**：在 `Contacts` 表新增一列並設定欄位驗證；`id` 保護。

### 2) 讀取/查詢
- **GET** `/contacts?updated_since={ts}&include_deleted={bool}&limit={n}&page_token={token}`
- **用途**：App 重新載入、桌面端同步；預設 `include_deleted=false`。
- **Response**：`200 OK`，回傳分頁資料與 `next_page_token`。
- **試算表映射**：可透過篩選視圖或腳本分頁讀取，需確保排序欄位穩定（`updated_at`）。

### 3) 更新名片
- **PUT** `/contacts/{id}`
- **Request body**：可更新除 `id` 外欄位；自動刷新 `updated_at`。
- **Response**：`200 OK` with updated contact。
- **併發規則**：最後寫入者覆蓋；回傳最終 `updated_at` 供雙端校驗。

### 4) 軟刪除
- **POST** `/contacts/{id}/delete`
- **Request body**：`{ "reason": "user_request" }`（可選）
- **動作**：設 `is_deleted=true`、`deleted_at=now`，不移除影像檔；回傳 `200 OK`。

### 5) 影像上傳
- **POST** `/contacts/{id}/image`
- **流程**：取得上傳位置（Drive 目錄）；完成後回寫 `image_url` 至試算表。
- **失敗處理**：若上傳失敗，資料列保留，`image_url` 為空並標記待補。

### 6) 離線暫存同步
- **POST** `/sync/offline`
- **Request body**：暫存批次（新增/更新/刪除）列表。
- **行為**：逐筆上傳；成功移出佇列，失敗保留並回傳錯誤碼。

## 驗收要點
- 欄位驗證：Email/電話格式、必填欄位、`id` 唯一性。
- 安全：僅受邀帳號可操作指定試算表/Drive；不得使用公開連結。
- 一致性：最後寫入者覆蓋，`updated_at` 反映最終時間；軟刪除預設過濾。
- 效能：建立/讀取/更新需滿足規格中的延遲預算（OCR 預填 ≤5 秒，試算表讀寫 ≤2 秒）。
