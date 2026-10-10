/*
 * File: FeatureToggleExtended.java
 * Modification: 2026-10-10 - Tweakeroo toggles for tuning, pitch copy and pitch paste.
 */
package dev.miki.noteblocktweaks.integration;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * One toggle, using the same MaLiLib interfaces that Tweakeroo's Tweaks tab uses.
 * The Mixin integration registers this option for GUI, hotkeys and Tweakeroo persistence.
 */
public enum FeatureToggleExtended implements IHotkeyTogglable, IConfigNotifiable<IConfigBoolean> {
    TWEAK_NOTE_BLOCK_TUNER("tweakNoteBlockTuner", "N", false),
    TWEAK_NOTE_BLOCK_COPY_PITCH("tweakNoteBlockCopyPitch", "", true),
    TWEAK_NOTE_BLOCK_PASTE_PITCH("tweakNoteBlockPastePitch", "", true);

    private final String name;
    private final String defaultKey;
    private final boolean defaultValue;
    private final IKeybind keybind;
    private boolean value;
    private IValueChangeCallback<IConfigBoolean> valueCallback;

    FeatureToggleExtended(String name, String defaultKey, boolean defaultValue) {
        this.name = name;
        this.defaultKey = defaultKey;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.keybind = KeybindMulti.fromStorageString(defaultKey, KeybindSettings.DEFAULT);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
    }

    @Override
    public ConfigType getType() { return ConfigType.HOTKEY; }

    @Override
    public String getName() { return name; }

    @Override
    public String getConfigGuiDisplayName() { return getName(); }

    @Override
    public String getPrettyName() { return this.name; }

    @Override
    public String getTranslatedName() {
        return StringUtils.getTranslatedOrFallback(this.name, this.getPrettyName());
    }

    @Override
    public void setPrettyName(String prettyName) { /* No mutable display label required. */ }

    @Override
    public void setTranslatedName(String translatedName) { /* Language assets provide the label. */ }

    @Override
    public void setComment(String comment) { /* Language assets provide the comment. */ }

    @Override
    public String getComment() { return this.name + ".comment"; }

    @Override
    public IKeybind getKeybind() { return this.keybind; }

    @Override
    public boolean getBooleanValue() { return this.value; }

    @Override
    public boolean getDefaultBooleanValue() { return defaultValue; }

    @Override
    public void setBooleanValue(boolean next) {
        if (this.value == next) return;
        this.value = next;
        this.onValueChanged();
    }

    @Override
    public void onValueChanged() {
        if (valueCallback != null) valueCallback.onValueChanged(this);
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback<IConfigBoolean> callback) {
        this.valueCallback = callback;
    }

    @Override
    public boolean isModified() { return this.value != defaultValue; }

    @Override
    public boolean isModified(String newValue) { return Boolean.parseBoolean(newValue) != defaultValue; }

    @Override
    public void resetToDefault() { setBooleanValue(defaultValue); }

    @Override
    public String getStringValue() { return Boolean.toString(this.value); }

    @Override
    public String getDefaultStringValue() { return Boolean.toString(defaultValue); }

    @Override
    public void setValueFromString(String text) { setBooleanValue(Boolean.parseBoolean(text)); }

    @Override
    public JsonElement getAsJsonElement() { return new JsonPrimitive(value); }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            setBooleanValue(element.getAsBoolean());
        }
    }
}
