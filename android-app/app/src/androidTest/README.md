# AndroidTest 指南（US1 基線）

- 儀表測試尚未撰寫，待 US1 假服務/真 API 整合後補齊。
- 目前覆蓋率驗證以 Jacoco `jacocoTestCoverageVerification` 寬鬆門檻 0.1 作為暫定 baseline，後續可視實作提升至規格要求（>=90% 陳述、>=75% 分支）。
- 執行建議：
  - 單元測試與報表：`./gradlew :app:testDebugUnitTest :app:jacocoTestReport`
  - Lint/Detekt：`./gradlew :app:detekt`
