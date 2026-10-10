/*
 * File: MixinTweakerooConfigs.java
 * Modification: 2026-10-10 - Include our MaLiLib toggle in Tweakeroo save/load lists.
 */
package dev.miki.noteblocktweaks.mixin;

import dev.miki.noteblocktweaks.integration.FeatureToggleExtended;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.tweakeroo.config.Configs;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(value = Configs.class, remap = false)
public abstract class MixinTweakerooConfigs {
    @ModifyArg(method = "loadFromFile", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/malilib/config/ConfigUtils;readHotkeyToggleOptions(Lcom/google/gson/JsonObject;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
            ordinal = 1), index = 3)
    private static List<? extends IHotkeyTogglable> addLoadToggles(List<? extends IHotkeyTogglable> original) {
        return append(original);
    }

    @ModifyArg(method = "saveToFile", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/malilib/config/ConfigUtils;writeHotkeyToggleOptions(Lcom/google/gson/JsonObject;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
            ordinal = 1), index = 3)
    private static List<? extends IHotkeyTogglable> addSaveToggles(List<? extends IHotkeyTogglable> original) {
        return append(original);
    }

    private static List<IHotkeyTogglable> append(List<? extends IHotkeyTogglable> original) {
        List<IHotkeyTogglable> result = new ArrayList<>(original);
        for (IHotkeyTogglable option : FeatureToggleExtended.values()) {
            if (!result.contains(option)) result.add(option);
        }
        return result;
    }
}
