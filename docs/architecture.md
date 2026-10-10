# NoteBlock Tweaks architecture

This document describes the implementation of NoteBlock Tweaks `0.3.0+mc1.21.8` for maintainers. User-facing setup and controls are in the [README](../README.md).

## Scope and invariants

- The mod runs only in the Fabric client environment.
- A note block pitch is always an integer from `0` through `24`.
- The pitch clipboard stores only the pitch, never an instrument or a complete block state.
- A remote server is authoritative. The client never persists a remote block-state change locally.
- A dedicated server must not install this mod.

## Components

| Component | Responsibility |
| --- | --- |
| `NoteBlockTweaksClient` | Client entrypoint; registers tick, keybinding, and block-use hooks. |
| `FeatureToggleExtended` | Tweakeroo/MaLiLib-compatible toggles for tuning, copy, and paste. |
| `MixinTweakeroo*` | Adds the toggle collection and keybinds to Tweakeroo without replacing its existing configuration. |
| `NoteTunerScreen` | Renders the 25-pitch picker and clipboard buttons. |
| `TuningController` | Validates targets, routes requests, and drives remote interaction state. |
| `IntegratedServerTuner` | Schedules an authoritative change on the integrated server for singleplayer. |
| `PitchMath` / `NotePitchClipboard` | Isolated pitch validation, names, wrapping, and session clipboard state. |
| `ModConfig` | Loads `config/noteblocktweaks.json`, clamps safe timing ranges, and falls back to defaults on invalid JSON. |

## Interaction flow

```text
empty-hand note-block use
        │
        ├─ tuner disabled / invalid target ───────────────► vanilla handling
        │
        └─ tuner enabled ─► UseBlockCallback returns FAIL
                                │
                                └─► NoteTunerScreen (0–24 picker)
                                           │
                                           └─► TuningController.begin()
                                                    │
                         ┌──────────────────────────┴──────────────────────────┐
                         │                                                     │
                 integrated server                                        remote server
                         │                                                     │
          IntegratedServerTuner.schedule()                         tick-driven vanilla use
                         │                                                     │
      server-thread validation + setBlockState()          one interaction → await state ack
                                                                      │
                                              target reached / timeout / interruption → stop
```

`UseBlockCallback` returns `FAIL` when it opens the picker. This is intentional: Fabric's success result would allow the same click to reach vanilla and increment the note before the user has chosen a pitch.

## Local and remote tuning

When `instantSingleplayer` is enabled and an integrated server is available, `IntegratedServerTuner` validates the player, world, position, distance, main hand, and note-block state on the server thread before calling `setBlockState`. The standard server update then synchronizes the result to the client.

Otherwise, `TuningController` sends at most one accepted vanilla `interactBlock` call at a time. It waits for the client world to observe the expected next pitch before sending another request. It stops if the target changes, the player changes dimension or moves out of range, the main hand becomes non-empty, the response times out, or 25 interactions have been sent. This keeps multiplayer behavior bounded and server-authoritative.

## Mixin integration

`noteblocktweaks.mixins.json` declares three required client mixins against Tweakeroo. They append the extended toggles to Tweakeroo's configuration and register the related MaLiLib keybinds. The input-handler mixin uses `@Pseudo` and `remap = false` because its target comes from another mod rather than Minecraft mappings.

## Configuration and persistence

`ModConfig` writes defaults only when its file does not exist. If the existing file cannot be read, it retains the file and uses in-memory defaults for the current session. Timing values are clamped to prevent zero-delay interaction loops or unbounded acknowledgements.

The pitch clipboard is session-only. It is cleared when the client no longer has a world.

## Verification

Run the complete automated suite with Java 21:

```bash
./gradlew clean test check build --offline --no-daemon
```

`PitchMathTest` covers pitch names, wrapping, and valid pitch pairs. `NotePitchClipboardTest` covers initial state, boundaries, overwrite behavior, and clearing. Automated tests do not replace manual in-game verification of Tweakeroo configuration, GUI input, integrated-server synchronization, and remote-server interaction acknowledgements.
