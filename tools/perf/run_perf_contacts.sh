#!/usr/bin/env bash
# 用途：量測 US1 掃描→暫存→排程上傳→假服務回應的手動耗時。
# 前置：已連線裝置/模擬器，可操作 US1 流程；本腳本不修改專案或裝置設定。

echo "US1 效能量測（手動）"
echo "1) 依提示操作 App：掃描→暫存→排程上傳→等待假服務回應"
echo "2) 每完成一步按 Enter 紀錄時間"

read -p "按 Enter 開始量測..." _
start=$(date +%s)
read -p "影像取得並暫存完成後按 Enter..." _
mid1=$(date +%s)
read -p "排程建立並開始上傳後按 Enter..." _
mid2=$(date +%s)
read -p "假服務回應並確認結果後按 Enter..." _
end=$(date +%s)

delta1=$((mid1-start))
delta2=$((mid2-mid1))
delta3=$((end-mid2))
total=$((end-start))

echo "===== 量測結果（秒）====="
echo "影像→暫存：$delta1"
echo "暫存→排程：$delta2"
echo "排程→回應：$delta3"
echo "總耗時：$total"
