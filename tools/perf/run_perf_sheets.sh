#!/usr/bin/env bash
# 用途：量測 US2 Phase1 讀取→快取→搜尋/排序/軟刪除的手動耗時。
# 前置：已連線裝置/模擬器，可操作 US2 Phase1 功能；本腳本不修改專案或裝置設定。

echo "US2 Phase1 效能量測（手動）"
echo "1) 依提示操作 App：首次讀取（含快取）→搜尋/排序→軟刪除並刷新"
echo "2) 每完成一步按 Enter 紀錄時間"

read -p "按 Enter 開始量測..." _
start=$(date +%s)
read -p "首次讀取/快取完成後按 Enter..." _
mid1=$(date +%s)
read -p "搜尋/排序結果呈現後按 Enter..." _
mid2=$(date +%s)
read -p "軟刪除並刷新列表後按 Enter..." _
end=$(date +%s)

deltaRead=$((mid1-start))
deltaSearch=$((mid2-mid1))
deltaDelete=$((end-mid2))
total=$((end-start))

echo "===== 量測結果（秒）====="
echo "讀取/快取：$deltaRead"
echo "搜尋/排序：$deltaSearch"
echo "軟刪除/刷新：$deltaDelete"
echo "總耗時：$total"
