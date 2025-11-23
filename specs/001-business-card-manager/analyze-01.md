  ## Specification Analysis Report

  | ID | Category | Severity | Location(s) | Summary | Recommendation |
  |----|----------|----------|-------------|---------|----------------|
  | C1 | Coverage Gap | CRITICAL | spec.md:73-85 (FR-004), tasks.md | 試算表欄位/驗證/保護（FR-004）無對應任務，核心資料源可能未佈
  建。 | 新增任務：佈建/驗證 Contacts 表頭、欄位驗證、id 保護與回歸檢查（automation/sheets_drive 或 android 佈建腳本）。 |
  | C2 | Coverage Gap | CRITICAL | spec.md:82-85 (FR-010), tasks.md | 受邀帳號存取與禁止公開連結未在任務中落實/驗證。 | 新增任務：檢
  查/設定 Drive/Sheets 權限、拒絕公開連結、測試非受邀帳號存取失敗。 |
  | C3 | Constitution | CRITICAL | constitution.md (Tests Define Reality), tasks.md | 未有任務確保 >=90% 陳述、>=75% 分支覆蓋的量測/
  守門；僅零散測試。 | 在 Phase 2/發佈前新增覆蓋率量測與門檻任務，將失敗納入 CI。 |
  | C4 | Constitution | CRITICAL | constitution.md (Performance Predictability), tasks.md | 效能預算（OCR ≤5s、讀寫 ≤2s、列表 5k）缺
  少量測/監測任務；T024 僅述 50k 抽樣未含指標/警戒。 | 新增效能基準量測任務（RUM/Profiler/合成測試），設定警戒門檻與報表。 |
  | H1 | Underspec Tasks | HIGH | tasks.md Phase 1–2,3 | 多個任務路徑以 .../「如有」占位，不符合可執行性與格式要求（需精確路徑）。 |
  以實際封包路徑替換占位，如 android/app/src/main/java/com/...、.github/workflows/android-ci.yml。 |
  | H2 | Coverage Gap | HIGH | spec.md:70-71 (edge cases), tasks.md | 影像上傳失敗重試、id 衝突提示等邊界情境無任務覆蓋。 | 補充錯誤
  處理/重試與衝突提示的實作與測試任務。 |
  | M1 | Coverage Gap | MEDIUM | spec.md:91-100 (回歸套件/遙測), tasks.md | 每日回歸與遙測告警流程未在任務中明確化；僅 T022 單次回
  歸。 | 添加排程回歸/告警設定任務，涵蓋異常率 ≥5% 通知。 |
  | M2 | Coverage Gap | MEDIUM | success criteria SC-001~004 | 成功指標（2 分鐘內入表、30 秒篩選、欄位完整率、錯誤率 <2%）無對應驗證
  任務。 | 為每項 SC 新增驗證/監測任務（合成流程測試或報表檢核）。 |

  ### Coverage Summary

  | Requirement Key | Has Task? | Task IDs | Notes |
  |-----------------|-----------|----------|-------|
  | fr-001-scan-ocr-create | Yes | T010-T013 | 覆蓋掃描/OCR/寫入/測試 |
  | fr-002-id-timestamps | Partial | T011 | 未明示時間戳格式/驗證 |
  | fr-003-image-upload | Yes | T012 | 無失敗重試任務 |
  | fr-004-contacts-schema | No | — | 缺佈建/驗證任務 |
  | fr-010-invite-only-access | No | — | 缺權限驗證任務 |
  | fr-012-mlkit-ocr | Yes | T010 | 需效能量測 |
  | fr-013-soft-delete | Yes | T015 | 需測試覆蓋 |
  | perf-budget | Partial | T024 | 未含 5s/2s 指標與監測 |
  | success-criteria | No | — | 無對應驗證任務 |

  Unmapped Tasks: T021 (sideload驗證) 與 T022 (回歸) 未明確對應需求鍵；T024 未綁定效能預算指標。

  Metrics:

  - Total Requirements（含 FR、品質/效能/遙測、SC）：26
  - Total Tasks：25
  - Coverage：~62%（關鍵 FR/NFR/SC 有任務者）
  - Ambiguities/Placeholders：多處 .../「如有」路徑
  - Critical Issues：4

  ## Next Actions

  - 先修復 CRITICAL：補齊 FR-004、FR-010 任務；新增覆蓋率守門與效能量測任務，避免違反憲章。
  - 其次修正路徑占位與邊界情境任務，並為成功指標建立驗證。
  - 準備好後再進行實作，避免重工。