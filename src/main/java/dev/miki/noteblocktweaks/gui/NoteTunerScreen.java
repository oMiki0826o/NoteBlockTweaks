/*
 * File: NoteTunerScreen.java
 * Modification: 2026-10-10 - Add pitch copy/paste buttons to 25-note tuner GUI.
 */
package dev.miki.noteblocktweaks.gui;

import dev.miki.noteblocktweaks.client.NoteBlockTweaksClient;
import dev.miki.noteblocktweaks.integration.FeatureToggleExtended;
import dev.miki.noteblocktweaks.tuning.PitchMath;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

public final class NoteTunerScreen extends Screen {
    private final BlockHitResult originalHit;
    private int originalPitch;
    private String instrument;

    public NoteTunerScreen(BlockHitResult hit) {
        super(Text.translatable("screen.noteblocktweaks.title"));
        this.originalHit = hit;
    }

    @Override
    protected void init() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) { close(); return; }
        var state = mc.world.getBlockState(originalHit.getBlockPos());
        if (!(state.getBlock() instanceof NoteBlock)) { close(); return; }
        originalPitch = state.get(NoteBlock.NOTE);
        instrument = state.get(NoteBlock.INSTRUMENT).asString();
        final int cols = 5;
        final int space = 4;
        final int buttonWidth = Math.min(76, (width - 28 - cols * space) / cols);
        final int buttonHeight = 20;
        final int originX = (width - (cols * buttonWidth + (cols - 1) * space)) / 2;
        final int originY = Math.max(55, (height - 6 * (buttonHeight + space) - 20) / 2);
        for (int pitch = 0; pitch < PitchMath.NOTE_COUNT; pitch++) {
            final int selected = pitch;
            int row = pitch / cols;
            int col = pitch % cols;
            String label = String.format("%02d %s", pitch, PitchMath.nameOf(pitch));
            if (pitch == originalPitch) label = "● " + label;
            addDrawableChild(ButtonWidget.builder(Text.literal(label), button -> {
                NoteBlockTweaksClient.tuner().begin(mc, originalHit, selected);
                close();
            }).dimensions(originX + col * (buttonWidth + space),
                    originY + row * (buttonHeight + space), buttonWidth, buttonHeight).build());
        }
        int rowY = originY + 5 * (buttonHeight + space) + 6;
        int smallWidth = Math.max(60, buttonWidth);
        int total = smallWidth * 3 + space * 2;
        int rowX = (width - total) / 2;
        ButtonWidget copy = addDrawableChild(ButtonWidget.builder(
                Text.translatable("screen.noteblocktweaks.copy"), button -> {
                    NoteBlockTweaksClient.copyPitch(mc, originalPitch);
                    close();
                }).dimensions(rowX, rowY, smallWidth, buttonHeight).build());
        copy.active = FeatureToggleExtended.TWEAK_NOTE_BLOCK_COPY_PITCH.getBooleanValue();

        ButtonWidget paste = addDrawableChild(ButtonWidget.builder(
                Text.translatable("screen.noteblocktweaks.paste"), button -> {
                    NoteBlockTweaksClient.tuner().begin(mc, originalHit,
                            NoteBlockTweaksClient.clipboard().getPitch());
                    close();
                }).dimensions(rowX + smallWidth + space, rowY, smallWidth, buttonHeight).build());
        paste.active = FeatureToggleExtended.TWEAK_NOTE_BLOCK_PASTE_PITCH.getBooleanValue()
                && NoteBlockTweaksClient.clipboard().hasPitch();

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cancel"), button -> close())
                .dimensions(rowX + (smallWidth + space) * 2, rowY, smallWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("screen.noteblocktweaks.current", PitchMath.nameOf(originalPitch), originalPitch),
                width / 2, 27, 0xDDDDDD);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("screen.noteblocktweaks.instrument", instrument), width / 2, 39, 0xBBBBBB);
    }

    @Override
    public boolean shouldPause() { return false; }
}
