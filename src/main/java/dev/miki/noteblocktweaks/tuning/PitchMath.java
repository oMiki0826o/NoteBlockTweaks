/*
 * File: PitchMath.java
 * Modification: 2026-10-10 - Centralize vanilla 25-step pitch conversion.
 */
package dev.miki.noteblocktweaks.tuning;

public final class PitchMath {
    public static final int NOTE_COUNT = 25;
    private static final String[] NOTES = {
            "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4",
            "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5",
            "D5", "D#5", "E5", "F5", "F#5"
    };

    private PitchMath() { }

    public static int clicksToReach(int current, int target) {
        requirePitch(current);
        requirePitch(target);
        return (target - current + NOTE_COUNT) % NOTE_COUNT;
    }

    public static int nextPitch(int pitch) {
        requirePitch(pitch);
        return (pitch + 1) % NOTE_COUNT;
    }

    public static String nameOf(int pitch) {
        requirePitch(pitch);
        return NOTES[pitch];
    }

    private static void requirePitch(int pitch) {
        if (pitch < 0 || pitch >= NOTE_COUNT) {
            throw new IllegalArgumentException("Note pitch must be between 0 and 24: " + pitch);
        }
    }
}
