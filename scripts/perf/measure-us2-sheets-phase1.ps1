# 使用說明：
# - 目的：量測 US2 Phase1「Sheets 讀取→快取→搜尋/排序/軟刪除」的手動耗時。
# - 前置：模擬器/實機已連線，App 已具備 US2 Phase1 功能；不修改專案或裝置設定。
# - 步驟：依提示按 Enter 紀錄讀取/快取/搜尋/軟刪除耗時，輸出統計。

Write-Host "US2 Phase1 效能量測開始：請依提示操作 App，按 Enter 繼續。"

$samples = @()
$stepNames = @(
    "首次讀取（含分頁/快取）完成",
    "搜尋/排序結果呈現",
    "軟刪除並刷新列表"
)

while ($true) {
    $timestamps = @()
    foreach ($step in $stepNames) {
        Read-Host "完成步驟「$step」後按 Enter"
        $timestamps += Get-Date
    }
    $deltaRead = ($timestamps[1] - $timestamps[0]).TotalSeconds
    $deltaSearch = ($timestamps[2] - $timestamps[1]).TotalSeconds
    $samples += [PSCustomObject]@{
        ReadCache = $deltaRead
        SearchSort = $deltaSearch
        Total = $deltaRead + $deltaSearch
    }
    $cont = Read-Host "是否繼續下一輪量測？(y/n)"
    if ($cont -ne "y") { break }
}

if ($samples.Count -eq 0) {
    Write-Host "未記錄任何樣本。"
    exit 0
}

$count = $samples.Count
$totalSum = ($samples | Measure-Object -Property Total -Sum).Sum
$avgTotal = $totalSum / $count
$maxTotal = ($samples | Measure-Object -Property Total -Maximum).Maximum

Write-Host "===== 量測結果 ====="
Write-Host "樣本數：$count"
Write-Host "總耗時(秒)：$totalSum"
Write-Host "平均總耗時(秒)：$([Math]::Round($avgTotal,2))"
Write-Host "最大單次耗時(秒)：$maxTotal"
Write-Host "詳情（秒）："
$samples | Format-Table -AutoSize
