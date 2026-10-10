/*
 * File: MixinTweakerooInputHandler.java
 * Modification: 2026-10-10 - Register our keybind in Tweakeroo's MaLiLib keybind manager.
 */
package dev.miki.noteblocktweaks.mixin;

import dev.miki.noteblocktweaks.integration.FeatureToggleExtended;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.tweakeroo.event.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = InputHandler.class, remap = false)
public abstract class MixinTweakerooInputHandler {
    @Inject(method = "addKeysToMap", at = @At("RETURN"))
    private void registerNoteTunerHotkey(IKeybindManager manager, CallbackInfo ci) {
        for (FeatureToggleExtended toggle : FeatureToggleExtended.values()) {
            manager.addKeybindToMap(toggle.getKeybind());
        }
    }
}
