/*
 * File: NoteBlockTweaksClient.java
 * Modification: 2026-10-10 - GUI tuning, pitch clipboard actions and integrated server routing.
 */
package dev.miki.noteblocktweaks.client;

import dev.miki.noteblocktweaks.config.ModConfig;
import dev.miki.noteblocktweaks.integration.FeatureToggleExtended;
import dev.miki.noteblocktweaks.gui.NoteTunerScreen;
import dev.miki.noteblocktweaks.tuning.NotePitchClipboard;
import dev.miki.noteblocktweaks.tuning.PitchMath;
import dev.miki.noteblocktweaks.tuning.TuningController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.lwjgl.glfw.GLFW;

public final class NoteBlockTweaksClient implements ClientModInitializer {
    private static TuningController tuner;
    private static final NotePitchClipboard CLIPBOARD = new NotePitchClipboard();

    public static TuningController tuner() { return tuner; }
    public static NotePitchClipboard clipboard() { return CLIPBOARD; }

    public static void copyPitch(MinecraftClient client, int pitch) {
        if (!FeatureToggleExtended.TWEAK_NOTE_BLOCK_COPY_PITCH.getBooleanValue()) return;
        CLIPBOARD.copy(pitch);
        if (client.player != null) {
            client.player.sendMessage(Text.translatable("message.noteblocktweaks.copied",
                    PitchMath.nameOf(pitch), pitch), true);
        }
    }

    @Override
    public void onInitializeClient() {
        ModConfig config = ModConfig.load();
        tuner = new TuningController(config);
        KeyBinding stop = key("key.noteblocktweaks.stop");
        KeyBinding copy = key("key.noteblocktweaks.copy");
        KeyBinding paste = key("key.noteblocktweaks.paste");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) { CLIPBOARD.clear(); tuner.cancel(); }
            while (stop.wasPressed()) {
                tuner.cancel();
                notice(client, "message.noteblocktweaks.stopped");
            }
            while (copy.wasPressed()) {
                if (FeatureToggleExtended.TWEAK_NOTE_BLOCK_COPY_PITCH.getBooleanValue()) {
                    BlockHitResult hit = targetedNoteBlock(client);
                    if (hit != null) copyPitch(client,
                            client.world.getBlockState(hit.getBlockPos()).get(NoteBlock.NOTE));
                }
            }
            while (paste.wasPressed()) {
                if (FeatureToggleExtended.TWEAK_NOTE_BLOCK_PASTE_PITCH.getBooleanValue()) {
                    BlockHitResult hit = targetedNoteBlock(client);
                    if (hit != null) {
                        if (CLIPBOARD.hasPitch()) tuner.begin(client, hit, CLIPBOARD.getPitch());
                        else notice(client, "message.noteblocktweaks.clipboard_empty");
                    }
                }
            }
            tuner.tick(client);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!world.isClient() || !FeatureToggleExtended.TWEAK_NOTE_BLOCK_TUNER.getBooleanValue()
                    || tuner.isForwardingInteraction() || hand != Hand.MAIN_HAND
                    || !world.getBlockState(hit.getBlockPos()).isOf(Blocks.NOTE_BLOCK)) {
                return ActionResult.PASS;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != player || client.currentScreen != null || player.isSpectator()) {
                return ActionResult.PASS;
            }
            if (config.requireEmptyMainHand && !player.getMainHandStack().isEmpty()) {
                return ActionResult.PASS;
            }
            client.setScreen(new NoteTunerScreen(hit));
            // Fabric SUCCESS sends the original use packet; FAIL cancels it entirely.
            // Without FAIL the first GUI-opening click also increments the note.
            return ActionResult.FAIL;
        });
    }

    private static KeyBinding key(String translationKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey,
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.noteblocktweaks"));
    }

    private static BlockHitResult targetedNoteBlock(MinecraftClient client) {
        if (client.world == null || client.player == null || client.currentScreen != null
                || client.player.isSpectator() || !(client.crosshairTarget instanceof BlockHitResult hit)
                || !client.world.getBlockState(hit.getBlockPos()).isOf(Blocks.NOTE_BLOCK)) return null;
        if (client.player.squaredDistanceTo(hit.getPos()) > 25.0) return null;
        return hit;
    }

    private static void notice(MinecraftClient client, String key) {
        if (client.player != null) client.player.sendMessage(Text.translatable(key), true);
    }
}
