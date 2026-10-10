/*
 * File: TuningController.java
 * Modification: 2026-10-10 - Integrated server instant tuning; remote vanilla interaction tuning.
 */
package dev.miki.noteblocktweaks.tuning;

import dev.miki.noteblocktweaks.config.ModConfig;
import dev.miki.noteblocktweaks.integration.FeatureToggleExtended;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class TuningController {
    private static final double MAX_DISTANCE_SQUARED = 25.0;
    private static final int MAX_SENT_INTERACTIONS = 25;

    private final ModConfig config;
    private BlockPos targetPos;
    private RegistryKey<World> targetDimension;
    private BlockHitResult originalHit;
    private int targetPitch;
    private int expectedPitch;
    private int remainingDelay;
    private int remainingTimeout;
    private int sentInteractions;
    private boolean awaitingUpdate;
    private boolean forwardingInteraction;

    public TuningController(ModConfig config) { this.config = config; }

    public boolean isForwardingInteraction() { return forwardingInteraction; }

    public void begin(MinecraftClient client, BlockHitResult hit, int desiredPitch) {
        if (client.player == null || client.world == null || client.interactionManager == null
                || client.player.isSpectator() || desiredPitch < 0 || desiredPitch >= PitchMath.NOTE_COUNT) return;
        if (!client.world.getBlockState(hit.getBlockPos()).isOf(Blocks.NOTE_BLOCK)) return;
        if (client.player.squaredDistanceTo(hit.getPos()) > MAX_DISTANCE_SQUARED) return;
        if (config.requireEmptyMainHand && !client.player.getMainHandStack().isEmpty()) {
            client.player.sendMessage(Text.translatable("message.noteblocktweaks.empty_hand"), true);
            return;
        }
        cancel();
        // Local worlds have an authoritative server in the same JVM; schedule a single
        // block-state change there. Remote worlds cannot do this without a server mod.
        if (config.instantSingleplayer && IntegratedServerTuner.schedule(
                client, hit, desiredPitch, config, MAX_DISTANCE_SQUARED)) return;

        targetPos = hit.getBlockPos().toImmutable();
        targetDimension = client.world.getRegistryKey();
        originalHit = hit;
        targetPitch = desiredPitch;
        expectedPitch = client.world.getBlockState(targetPos).get(NoteBlock.NOTE);
        remainingDelay = 0;
        remainingTimeout = 0;
    }

    public void cancel() {
        targetPos = null;
        targetDimension = null;
        originalHit = null;
        awaitingUpdate = false;
        forwardingInteraction = false;
        sentInteractions = 0;
        remainingDelay = 0;
        remainingTimeout = 0;
    }

    public void tick(MinecraftClient client) {
        if (targetPos == null) return;
        if ((!FeatureToggleExtended.TWEAK_NOTE_BLOCK_TUNER.getBooleanValue()
                && !FeatureToggleExtended.TWEAK_NOTE_BLOCK_PASTE_PITCH.getBooleanValue())
                || client.player == null || client.world == null || client.interactionManager == null
                || !client.world.getRegistryKey().equals(targetDimension)) {
            cancel();
            return;
        }
        if (client.player.squaredDistanceTo(Vec3d.ofCenter(targetPos)) > MAX_DISTANCE_SQUARED
                || (config.requireEmptyMainHand && !client.player.getMainHandStack().isEmpty())) {
            stopWithMessage(client, "message.noteblocktweaks.interrupted");
            return;
        }
        BlockState state = client.world.getBlockState(targetPos);
        if (!state.isOf(Blocks.NOTE_BLOCK)) {
            stopWithMessage(client, "message.noteblocktweaks.interrupted");
            return;
        }
        int observedPitch = state.get(NoteBlock.NOTE);
        if (awaitingUpdate) {
            if (observedPitch == expectedPitch) {
                awaitingUpdate = false;
                remainingDelay = config.clickDelayTicks;
            } else if (--remainingTimeout <= 0) {
                stopWithMessage(client, "message.noteblocktweaks.timeout");
                return;
            } else {
                return;
            }
        }
        if (observedPitch == targetPitch) {
            client.player.sendMessage(Text.translatable("message.noteblocktweaks.finished", PitchMath.nameOf(targetPitch)), true);
            cancel();
            return;
        }
        if (remainingDelay-- > 0) return;
        if (sentInteractions >= MAX_SENT_INTERACTIONS) {
            stopWithMessage(client, "message.noteblocktweaks.interrupted");
            return;
        }
        expectedPitch = PitchMath.nextPitch(observedPitch);
        forwardingInteraction = true;
        ActionResult result;
        try {
            result = client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, originalHit);
        } finally {
            forwardingInteraction = false;
        }
        if (!result.isAccepted()) {
            stopWithMessage(client, "message.noteblocktweaks.interrupted");
            return;
        }
        sentInteractions++;
        awaitingUpdate = true;
        remainingTimeout = config.acknowledgementTimeoutTicks;
    }

    private void stopWithMessage(MinecraftClient client, String translationKey) {
        if (client.player != null) client.player.sendMessage(Text.translatable(translationKey), true);
        cancel();
    }
}
