/*
 * File: IntegratedServerTuner.java
 * Modification: 2026-10-10 - Modify note pitch on the integrated server thread in local worlds.
 */
package dev.miki.noteblocktweaks.tuning;

import dev.miki.noteblocktweaks.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * The physical client hosts an integrated server for a local world. Never mutate a
 * ClientWorld here: its block state is not authoritative. Every server-world change
 * runs on the logical server thread, with permission and reach checks repeated there.
 */
public final class IntegratedServerTuner {
    private IntegratedServerTuner() { }

    public static boolean schedule(MinecraftClient client, BlockHitResult hit, int pitch,
                                   ModConfig config, double maxDistanceSquared) {
        IntegratedServer server = client.getServer();
        if (!client.isInSingleplayer() || server == null || client.world == null || client.player == null) {
            return false;
        }
        RegistryKey<World> dimension = client.world.getRegistryKey();
        BlockPos position = hit.getBlockPos().toImmutable();
        UUID playerId = client.player.getUuid();

        // Thread handoff is mandatory: local server state is not safe to write from a GUI callback.
        server.execute(() -> {
            boolean success = false;
            ServerWorld world = server.getWorld(dimension);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
            if (world != null && player != null && player.getWorld() == world
                    && !player.isSpectator() && player.getAbilities().allowModifyWorld
                    && player.canModifyAt(world, position)
                    && player.squaredDistanceTo(Vec3d.ofCenter(position)) <= maxDistanceSquared
                    && (!config.requireEmptyMainHand || player.getMainHandStack().isEmpty())
                    && world.isChunkLoaded(position)) {
                BlockState previous = world.getBlockState(position);
                if (previous.isOf(Blocks.NOTE_BLOCK)) {
                    if (previous.get(NoteBlock.NOTE) == pitch) {
                        success = true;
                    } else {
                        success = world.setBlockState(position, previous.with(NoteBlock.NOTE, pitch), Block.NOTIFY_ALL);
                        if (success) {
                            // Match the audible note preview behavior after a successful adjustment.
                            world.addSyncedBlockEvent(position, Blocks.NOTE_BLOCK, 0, 0);
                        }
                    }
                }
            }
            final boolean completed = success;
            client.execute(() -> {
                // Do not display a stale message if the user has since left this local world.
                if (client.getServer() != server || client.player == null) return;
                client.player.sendMessage(completed
                        ? Text.translatable("message.noteblocktweaks.finished", PitchMath.nameOf(pitch))
                        : Text.translatable("message.noteblocktweaks.interrupted"), true);
            });
        });
        return true;
    }
}
