# 任務：名片管理系統第一版（side-load APK 發佈）

**輸入**：`spec.md`、`plan.md`、`research.md`、`data-model.md`、`contracts/`  
**前置需求**：完成 Phase 1–2（基礎建設），不涉及 Google Play 上架；僅產出簽署 APK 供 side-load。

## 憲章強制品質任務（共通）
- [ ] Q001 [P] 確認模組 ownership、架構說明與 lint/detekt 設定（程式碼品質監護）。
- [ ] Q002 [P] 擴充自動化測試達 >=90% 陳述 / >=75% 分支（同步/離線/重試核心）。
- [ ] Q003 驗證 UX 一致性與無障礙（WCAG 2.1 AA），記錄視覺回歸結果。
- [ ] Q004 量測效能預算：OCR ≤5 秒、讀寫 ≤2 秒、列表 5k 筆不逾時；異常率 ≥5% 觸發告警。
- [ ] Q005 確認所有文件/指南為繁體中文並同步更新（語言治理）。

## Phase 3：使用者故事 1 - 手機掃描並建檔（P1）
- [ ] T101 [US1] 實作掃描/OCR 流程（android/app/src/main/...），含 ML Kit、預填與手動修正 UI。
- [ ] T102 [US1] 實作名片建立服務：生成 `id`、寫入 Room 暫存與 Sheets 同步佇列；處理影像上傳失敗標記。
- [ ] T103 [US1] 儀表/整合測試：掃描→儲存→同步到試算表（android/app/src/androidTest/...）；契約測試比對 `contracts/contacts-sync.md`。

## Phase 4：使用者故事 2 - 桌面編修與查詢（P1）
- [ ] T201 [US2] 實作資料讀取/更新：依 `id` 更新列、刷新 `updated_at`、軟刪除欄位；支援篩選/排序（依 spec 欄位）。
- [ ] T202 [US2] 建立回寫腳本或 API 呼叫封裝，確保試算表欄位驗證與保護規則有效。
- [ ] T203 [US2] 整合測試：桌面更新 → App 重新整理可見最新內容；軟刪除預設過濾。

## Phase 5：使用者故事 3 - 帳號安全與同步一致性（P2）
- [ ] T301 [US3] OAuth 權限管理：選擇/變更試算表與 Drive 目錄，僅受邀帳號；最小範圍權限。
- [ ] T302 [US3] 衝突處理：最後寫入者覆蓋，`updated_at` 反映最終時間；離線多筆佇列逐筆上傳與重試。
- [ ] T303 [US3] 安全/一致性測試：撤銷授權提示重新登入；同筆同時更新的最終內容與時間戳驗證。

## 發佈與 side-load（不含 Play 上架）
- [ ] D401 產出簽署 APK：設定簽章金鑰，執行 `./gradlew assembleRelease`，產物 `app/build/outputs/apk/release/app-release.apk`。
- [ ] D402 驗證 side-load 安裝：`adb install -r ...` 或實體機啟用未知來源後手動安裝；記錄安裝/啟動/授權流程。
- [ ] D403 回歸測試：以簽署 APK 執行全套整合/契約/離線重試測試，確保與 debug 版一致。

## Phase N：潤飾與跨故事
- [ ] N501 清理程式碼與文件，確保 spec/plan/quickstart 同步。
- [ ] N502 追加效能與遙測優化（快取/分頁），針對 50k 列場景做抽樣測試。
- [ ] N503 安全性檢查：權限範圍、token 管理、日誌脫敏。
