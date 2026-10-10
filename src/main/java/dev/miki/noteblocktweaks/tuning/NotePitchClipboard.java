/*
 * File: NotePitchClipboard.java
 * Modification: 2026-10-10 - Hold a single note pitch in a session-only clipboard.
 */
package dev.miki.noteblocktweaks.tuning;

/** Stores only the note-block pitch; not an instrument or complete block state. */
public final class NotePitchClipboard {
    private int pitch = -1;

    public boolean hasPitch() { return pitch >= 0; }

    public int getPitch() {
        if (!hasPitch()) throw new IllegalStateException("No note pitch has been copied");
        return pitch;
    }

    public void copy(int newPitch) {
        if (newPitch < 0 || newPitch >= PitchMath.NOTE_COUNT) {
            throw new IllegalArgumentException("Pitch must be 0-24");
        }
        pitch = newPitch;
    }

    public void clear() { pitch = -1; }
}
