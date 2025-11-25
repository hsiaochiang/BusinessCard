# 使用說明：
# - 目的：量測 US1「掃描→暫存→排程上傳→假服務回應」的手動耗時。
# - 前置：已啟動模擬器或連線實機；App 已可執行 US1 流程；不修改專案或裝置設定。
# - 步驟：依提示按 Enter 紀錄各階段時間，輸出總結（次數/總計/平均/最大）。

Write-Host "US1 效能量測開始：請依提示操作 App，按 Enter 繼續。"

$samples = @()
$stepNames = @("影像取得→暫存完成", "暫存→排程建立", "排程→假服務回應")

while ($true) {
    $durations = @()
    foreach ($step in $stepNames) {
        Read-Host "完成步驟「$step」後按 Enter"
        $timestamp = Get-Date
        $durations += $timestamp
    }
    # 計算區段差值
    $delta1 = ($durations[1] - $durations[0]).TotalSeconds
    $delta2 = ($durations[2] - $durations[1]).TotalSeconds
    $delta3 = 0 # 若需更細分可再增加，這裡簡化為兩段
    $samples += [PSCustomObject]@{
        Step1 = $delta1
        Step2 = $delta2
        Step3 = $delta3
        Total = $delta1 + $delta2 + $delta3
    }
    $cont = Read-Host "是否繼續下一輪量測？(y/n)"
    if ($cont -ne "y") { break }
}

if ($samples.Count -eq 0) {
    Write-Host "未記錄任何樣本。"
    exit 0
}

$totalCount = $samples.Count
$totalSum = ($samples | Measure-Object -Property Total -Sum).Sum
$maxTotal = ($samples | Measure-Object -Property Total -Maximum).Maximum
$avgTotal = $totalSum / $totalCount

Write-Host "===== 量測結果 ====="
Write-Host "樣本數：$totalCount"
Write-Host "總耗時(秒)：$totalSum"
Write-Host "平均總耗時(秒)：$([Math]::Round($avgTotal,2))"
Write-Host "最大單次耗時(秒)：$maxTotal"
Write-Host "詳情（秒）："
$samples | Format-Table -AutoSize
