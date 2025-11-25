# US3 手動驗收指引（安全與一致性 Stub）

## 目的
- 確認 US3 的授權／一致性 stub 介面與流程佈線無異常，尚未串真實 API。

## 前置
- 模擬器或實機已連線；App 版本含 US3 stub（PermissionChecker／ConsistencyPolicy／UseCase／ViewModel）。

## 驗收步驟
1) 啟動 App，執行基本導航（無需真實授權 UI），確保無崩潰。  
2) 以測試 session 模擬 AccessRequest（讀寫 Sheets/Drive），呼叫 ViewModel 的判斷接口：應回傳允許/拒絕的預設結果，不應拋例外。  
3) 以兩筆 ContactRecord 模擬衝突，呼叫 `SecurityViewModel.pickLatest`：應回傳 updated_at 較新的紀錄。  
4) 觀察 log/假 Telemetry，如有記錄應不影響流程。  

## 預期
- Stub 介面可被呼叫且不崩潰，預設決策可返回合理結果。  
- 無權限時回傳拒絕訊息，不應造成程式異常。  
- 衝突解析以 updated_at 較新者為準。
