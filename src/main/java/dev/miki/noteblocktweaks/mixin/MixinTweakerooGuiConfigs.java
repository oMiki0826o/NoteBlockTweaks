/*
 * File: MixinTweakerooGuiConfigs.java
 * Modification: 2026-10-10 - Append note block tuning toggle to Tweakeroo Tweaks tab.
 */
package dev.miki.noteblocktweaks.mixin;

import dev.miki.noteblocktweaks.integration.FeatureToggleExtended;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.BooleanHotkeyGuiWrapper;
import fi.dy.masa.tweakeroo.gui.GuiConfigs;
import java.util.ArrayList;
import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(value = GuiConfigs.class, remap = false)
public abstract class MixinTweakerooGuiConfigs {
    @ModifyArg(method = "getConfigs", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/malilib/gui/GuiConfigsBase$ConfigOptionWrapper;createFor(Ljava/util/Collection;)Ljava/util/List;",
            ordinal = 1), index = 0)
    private Collection<? extends IConfigBase> addTweakOption(Collection<? extends IConfigBase> options) {
        Collection<IConfigBase> extended = new ArrayList<>(options);
        for (FeatureToggleExtended toggle : FeatureToggleExtended.values()) {
            extended.add(new BooleanHotkeyGuiWrapper(toggle.getName(), toggle, toggle.getKeybind()));
        }
        return extended;
    }
}
