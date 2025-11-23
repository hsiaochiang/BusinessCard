# Data Model - 名片管理系統第一版

**來源**：`specs/001-business-card-manager/spec.md`  
**版本**：2025-11-22

## 實體與欄位

### ContactRecord
- `id` (string, required, unique)：格式 `yyyyMMddHHmmss` + 4 位隨機碼，來源於 App 或試算表腳本。
- `name` (string, required)：姓名。
- `company` (string, optional)：公司名稱。
- `title` (string, optional)：職稱。
- `email` (string, optional)：需符合 email 格式驗證。
- `phone` (string, optional)：需符合電話格式驗證（國碼 + 號碼）。
- `tags` (string/list, optional)：多選標籤；試算表可為逗號分隔或資料驗證清單。
- `notes` (string, optional)：備註。
- `source` (enum, required)：`scan` | `manual` | `import`。
- `image_url` (string, optional)：Google Drive 共享 URL。
- `created_at` (datetime, required)：ISO 8601（UTC+08:00，台北時區）。
- `updated_at` (datetime, required)：ISO 8601（UTC+08:00，台北時區）。
- `last_contact_at` (date, optional)：最近聯絡日期，格式 `YYYY-MM-DD`（UTC+08:00）。
- `is_deleted` (boolean, required, default false)。
- `deleted_at` (datetime, optional)：軟刪除時間，ISO 8601（UTC+08:00，台北時區）。

### ContactImage
- `file_name` (string, required)。
- `drive_id` (string, required)：Drive 物件 ID。
- `shared_url` (string, required)：對應 ContactRecord.image_url。
- `created_at` (datetime, required)：ISO 8601（UTC+08:00，台北時區）。
- `owner_account` (string, required)：上傳帳號（受邀）。

## 關聯
- ContactRecord 1:1 ContactImage（透過 `image_url`/`drive_id`），影像可為空；刪除時維持軟刪除，不移除影像檔。

## 驗證與規則
- `id` 唯一且不可更改；App/試算表均使用相同生成規則。
- `email`/`phone` 必須通過試算表欄位驗證與 App 端基本格式檢查。
- `is_deleted=true` 時，前端/查詢預設過濾；可於稽核或還原流程中顯示。
- 欄位驗證與表頭需在試算表 `Contacts` 事前佈建；`id` 欄需保護。

## 狀態 / 轉換
- 新增：建立 ContactRecord + 上傳 ContactImage（可延後重試）；寫入試算表並設定時間戳與來源。
- 更新：以 `id` 定位資料列，更新欄位並刷新 `updated_at`（ISO 8601，UTC+08:00）。
- 軟刪除：設定 `is_deleted=true` 與 `deleted_at`（ISO 8601，UTC+08:00）；後續查詢預設排除。
- 離線暫存：以 Room 儲存草稿與影像路徑；恢復上線後逐筆同步，同步成功即移除暫存並寫入試算表。
