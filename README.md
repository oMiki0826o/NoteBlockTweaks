# NoteBlock Tweaks

![Version](https://img.shields.io/badge/version-0.3.0%2Bmc1.21.8-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Stack](https://img.shields.io/badge/stack-Java%2021%20%7C%20Fabric-lightgrey)
![Minecraft](https://img.shields.io/badge/minecraft-1.21.8-orange)

[中文](#中文) | [English](#english)

---

## 中文

### 目錄

- [關於](#關於)
- [功能](#功能)
- [安裝](#安裝)
- [使用方式](#使用方式)
- [授權](#授權)

### 關於

NoteBlock Tweaks 是 Minecraft Java 1.21.8 的 Fabric 純客戶端模組, 為 Tweakeroo 加入音階盒調音介面與音高複製/貼上功能. 它支援單人世界的整合伺服器調音, 以及遠端伺服器上的正常互動式調音, 不會在多人伺服器直接修改方塊狀態.

### 功能

- 在 Tweakeroo Tweaks 中提供音階盒調音, 音高複製與音高貼上規則.
- 空手右鍵音階盒可開啟 0-24 音高選擇介面.
- 僅複製音高, 不複製樂器或支撐方塊設定.
- 單人世界可在整合伺服器執行緒安全地一次設定音高.
- 遠端伺服器會依設定以正常右鍵互動和狀態確認逐次調音.

### 安裝

1. 安裝 Minecraft Java 1.21.8 的 Fabric Loader, Fabric API, MaLiLib 與 Tweakeroo.
2. 從 Releases 下載 `noteblock-tweaks-0.3.0+mc1.21.8.jar`, 放入 Minecraft 的 `mods` 資料夾.
3. 使用 Java 21 啟動遊戲.

從原始碼建置:

```bash
./gradlew build
```

成品位於 `build/libs/`.

### 使用方式

1. 開啟 Tweakeroo 設定, 在 Tweaks 中啟用 `tweakNoteBlockTuner`.
2. 空手對音階盒按右鍵, 在介面中選擇目標音高.
3. 使用調音介面的複製與貼上按鈕, 或在 Minecraft 控制設定中為兩個動作設定快捷鍵.
4. 多人伺服器調音會等待每次伺服器狀態更新. 可設定停止自動調音快捷鍵以中止排程.

設定檔為 `config/noteblocktweaks.json`. `instantSingleplayer` 預設為 `true`; 設為 `false` 時, 單人世界也會使用正常右鍵互動.

### 授權

本專案採用 MIT License, 詳見 [LICENSE](./LICENSE).

---

## English

### Table of Contents

- [About](#about)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

### About

NoteBlock Tweaks is a client-side Fabric mod for Minecraft Java 1.21.8. It extends Tweakeroo with a note block tuning screen and pitch copy/paste actions. It supports integrated-server tuning in singleplayer and vanilla interaction-based tuning on remote servers without directly changing remote block states.

### Features

- Adds note block tuning, pitch copy, and pitch paste rules to Tweakeroo Tweaks.
- Opens a 0-24 pitch selection screen when an empty-handed player uses a note block.
- Copies only pitch, never the instrument or supporting-block configuration.
- Applies singleplayer changes safely on the integrated server thread.
- Tunes remote servers through paced vanilla interactions and state acknowledgements.

### Installation

1. Install Fabric Loader, Fabric API, MaLiLib, and Tweakeroo for Minecraft Java 1.21.8.
2. Download `noteblock-tweaks-0.3.0+mc1.21.8.jar` from Releases and place it in the Minecraft `mods` directory.
3. Launch the game with Java 21.

Build from source:

```bash
./gradlew build
```

The built JAR is written to `build/libs/`.

### Usage

1. Enable `tweakNoteBlockTuner` in Tweakeroo's Tweaks screen.
2. Use an empty main hand on a note block and select the target pitch.
3. Use the screen's copy and paste buttons, or bind the actions in Minecraft controls.
4. Remote-server tuning waits for each server update. Bind Stop Auto Tuning to cancel a scheduled operation.

Configuration is stored in `config/noteblocktweaks.json`. Set `instantSingleplayer` to `false` to use vanilla interactions in singleplayer too.

### License

This project is licensed under the MIT License. See [LICENSE](./LICENSE).
