/*
 * File: ModConfig.java
 * Modification: 2026-10-10 - Server mode selection and remote click pacing.
 */
package dev.miki.noteblocktweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {
    private static final Logger LOG = LoggerFactory.getLogger("NoteBlockTweaks");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("noteblocktweaks.json");

    public int clickDelayTicks = 3;
    public int acknowledgementTimeoutTicks = 40;
    public boolean requireEmptyMainHand = true;
    public boolean instantSingleplayer = true;

    public static ModConfig load() {
        if (!Files.isRegularFile(FILE)) {
            ModConfig config = new ModConfig();
            config.save();
            return config;
        }
        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            ModConfig config = GSON.fromJson(reader, ModConfig.class);
            if (config == null) {
                throw new JsonParseException("Missing JSON config body");
            }
            config.clickDelayTicks = clamp(config.clickDelayTicks, 1, 20);
            config.acknowledgementTimeoutTicks = clamp(config.acknowledgementTimeoutTicks, 10, 200);
            return config;
        } catch (IOException | JsonParseException e) {
            LOG.warn("Could not read config {}, using defaults without overwriting it", FILE, e);
            return new ModConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOG.error("Could not write config {}", FILE, e);
        }
    }

    private static int clamp(int value, int lower, int upper) {
        return Math.max(lower, Math.min(upper, value));
    }
}
