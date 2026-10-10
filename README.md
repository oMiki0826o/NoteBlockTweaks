# NoteBlock Tweaks

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8-orange)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/loader-Fabric-informational)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

[中文](#中文) | [English](#english)

## 中文

### 關於

NoteBlock Tweaks 是 Minecraft Java **1.21.8** 的 Fabric 純客戶端模組，為 Tweakeroo 加入音階盒調音介面、音高複製與音高貼上功能。模組不需要、也不應安裝於伺服器端。

在單人世界中，模組可透過整合伺服器安全設定目標音高；在多人世界中，則以正常的原版右鍵互動逐次調音，從不直接修改遠端伺服器的方塊狀態。

### 功能

- 在 Tweakeroo 的 Tweaks 頁面提供調音、複製音高與貼上音高三個開關。
- 空手右鍵音階盒，開啟包含 **0–24** 全部音高的選擇介面。
- 只複製音高；樂器類型與支撐方塊維持原狀。
- 透過原版控制設定綁定複製、貼上與停止自動調音快捷鍵。
- 多人調音會等待伺服器回傳每次音高更新，逾時、離開範圍或方塊改變時會中止。

### 需求

| 元件 | 需求 |
| --- | --- |
| Minecraft | Java Edition 1.21.8 |
| Java | 21 以上 |
| Loader | Fabric Loader 0.17.2 以上 |
| 相依模組 | Fabric API、MaLiLib 0.25.0-sakura.1 以上、Tweakeroo |

### 安裝

1. 安裝相同 Minecraft 版本的 Fabric Loader、Fabric API、MaLiLib 與 Tweakeroo。
2. 將 Release 的 `noteblock-tweaks-0.3.0+mc1.21.8.jar` 放入用戶端 `mods` 資料夾。
3. 以 Java 21 啟動遊戲。

> 不要將此 JAR 放入專用伺服器的 `mods` 資料夾。它宣告為 client-only 模組，伺服器端不提供功能。

### 使用方式

1. 開啟 Tweakeroo 設定，於 **Tweaks** 啟用 `tweakNoteBlockTuner`；預設快捷鍵為 `N`。
2. 空手對音階盒按右鍵，選擇目標音高。開啟介面的第一次點擊不會送出原版調音互動。
3. 在介面內使用 Copy pitch／Paste pitch，或在 Minecraft 的「控制」設定中為 Copy、Paste、Stop Automatic Tuning 指派按鍵。
4. 多人世界中保持在目標方塊五格內，並等待每一次伺服器狀態更新；要立即取消，使用 Stop Automatic Tuning。

設定檔位於 `config/noteblocktweaks.json`：

| 設定 | 預設值 | 說明 |
| --- | --- | --- |
| `clickDelayTicks` | `3` | 多人調音每次原版互動之間的間隔（1–20）。 |
| `acknowledgementTimeoutTicks` | `40` | 等待伺服器音高回應的時間（10–200）。 |
| `requireEmptyMainHand` | `true` | 只允許空主手開啟或執行調音。 |
| `instantSingleplayer` | `true` | 單人世界直接由整合伺服器設定音高；設為 `false` 可改用互動式調音。 |

### 從原始碼建置

需要 Java 21。macOS／Linux：

```bash
./gradlew clean test check build
```

成品位於 `build/libs/noteblock-tweaks-0.3.0+mc1.21.8.jar`。

### 授權

本專案採用 [MIT License](LICENSE)。

## English

### About

NoteBlock Tweaks is a client-side Fabric mod for Minecraft Java **1.21.8**. It extends Tweakeroo with a note-block tuning screen plus pitch copy and paste actions. It neither needs nor belongs on the server.

In singleplayer, it safely sets the target pitch through the integrated server. In multiplayer, it tunes through paced vanilla right-click interactions and never directly changes a remote server's block state.

### Features

- Adds tuning, pitch-copy, and pitch-paste toggles to Tweakeroo's Tweaks page.
- Opens a full **0–24** pitch picker when an empty-handed player uses a note block.
- Copies pitch only; the instrument and supporting blocks are untouched.
- Registers copy, paste, and stop-tuning keybinds in Minecraft controls.
- Waits for each server-side pitch update on remote servers and stops on timeout, range changes, or block changes.

### Requirements

| Component | Requirement |
| --- | --- |
| Minecraft | Java Edition 1.21.8 |
| Java | 21 or later |
| Loader | Fabric Loader 0.17.2 or later |
| Dependencies | Fabric API, MaLiLib 0.25.0-sakura.1 or later, and Tweakeroo |

### Installation

1. Install Fabric Loader, Fabric API, MaLiLib, and Tweakeroo for the same Minecraft version.
2. Place `noteblock-tweaks-0.3.0+mc1.21.8.jar` from Releases in the client's `mods` directory.
3. Start the game with Java 21.

> Do not place this JAR in a dedicated server's `mods` directory. The mod is declared client-only and provides no server-side feature.

### Usage

1. Open Tweakeroo's configuration and enable `tweakNoteBlockTuner` in **Tweaks**. Its default key is `N`.
2. Use a note block with an empty main hand, then choose the desired pitch. The click that opens the screen does not send a vanilla tuning interaction.
3. Use Copy pitch and Paste pitch in the screen, or assign keys for Copy, Paste, and Stop Automatic Tuning in Minecraft controls.
4. On a remote server, remain within five blocks of the target and wait for every server update. Use Stop Automatic Tuning to cancel immediately.

Configuration is stored in `config/noteblocktweaks.json`:

| Setting | Default | Meaning |
| --- | --- | --- |
| `clickDelayTicks` | `3` | Delay between remote vanilla interactions (1–20). |
| `acknowledgementTimeoutTicks` | `40` | Time to wait for a server pitch acknowledgement (10–200). |
| `requireEmptyMainHand` | `true` | Require an empty main hand for tuning actions. |
| `instantSingleplayer` | `true` | Tune through the integrated server; set to `false` to use interactions in singleplayer too. |

### Building from source

Java 21 is required. On macOS or Linux:

```bash
./gradlew clean test check build
```

The output is `build/libs/noteblock-tweaks-0.3.0+mc1.21.8.jar`.

### License

This project is licensed under the [MIT License](LICENSE).
