# NoteBlock Tweaks 開發架構（v0.3.0）

這是純客戶端 Fabric 模組，在本地世界可以透過同一個 JVM 中的整合伺服器直接調音，在外部伺服器只能使用正常右鍵互動。

## 調音路由

```text
GUI 音高選擇 / 快捷鍵貼上
          │
          └─ TuningController.begin()
                      │
                  安全前置檢查
                      │
           ┌──────────┴──────────┐
           │                     │
        本地世界              遠端伺服器
           │                     │
  IntegratedServerTuner     TuningController.tick
           │                     │
  integratedServer.execute()  每刻最多一個互動
           │                     │
  驗證玩家、距離、世界、權限      等待伺服器確認音高更新
           │                     │
  setBlockState()                直到完成或逾時
           │
  原版伺服器同步狀態
```

## 其他設計決策

- 客戶端的 `ClientWorld#setBlockState` 絕不用來執行持久化調音。
- 讀取音高使用 `NoteBlock.NOTE`，值域 0–24。
- 使用 Fabric `UseBlockCallback` 的 **FAIL**（非 SUCCESS），開啟 GUI 不應送出第一下互動封包。
- `FeatureToggleExtended` 分別定義調音、複製、貼上開關；快捷鍵切換仍由 Tweakeroo 管理。
- GUI 操作與鍵盤動作都是複製/貼上功能入口，兩個動作鍵以 Fabric KeyBinding 註冊於原版「控制」設定，預設未綁定。
- 外部伺服器的逐次點擊採取狀態確認與逾時機制；不得在未確認的情況下無限制重送。
- GUI 只複製音高，不修改樂器種類（由音階盒上下的方塊等環境決定）。

## 純 Java 測試

`PitchMathTest` 覆蓋 625 個音高轉換組合，`NotePitchClipboardTest` 檢查上下界、覆寫及清空。

## 驗收標準

1. Fabric 1.21.8 正常編譯，載入成功。
2. Tweakeroo → Tweaks 顯示三個設定，開關與快捷鍵可儲存。
3. 第一次右鍵僅開 GUI，不能先把音高增加 1。
4. 單人世界調音後重新進入世界，音高仍正確且其他玩家可同步看到。
5. 外部原版伺服器不裝模組，也能完成逐次調音；限制互動時中斷而不錯誤宣稱成功。
6. 複製/貼上支援音高 0 與 24；關閉相應開關時停止操作。
7. 安裝 Tweak Click、Fast Right Click 時，沒有規則列表覆寫與重複操作。
