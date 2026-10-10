/*
 * File: PitchMathTest.java
 * Modification: 2026-10-10 - Offline tests for pure pitch logic (no Minecraft deps).
 */
package dev.miki.noteblocktweaks.tuning;

public final class PitchMathTest {
    public static void main(String[] args) {
        assertEquals(0, PitchMath.clicksToReach(0, 0));
        assertEquals(24, PitchMath.clicksToReach(1, 0));
        assertEquals(1, PitchMath.clicksToReach(24, 0));
        assertEquals(14, PitchMath.clicksToReach(4, 18));
        for (int current = 0; current < PitchMath.NOTE_COUNT; current++) {
            for (int target = 0; target < PitchMath.NOTE_COUNT; target++) {
                int result = current;
                int clicks = PitchMath.clicksToReach(current, target);
                for (int i = 0; i < clicks; i++) result = PitchMath.nextPitch(result);
                assertEquals(target, result);
            }
        }
        assertEquals("F#3", PitchMath.nameOf(0));
        assertEquals("F#5", PitchMath.nameOf(24));
        System.out.println("PitchMath: 625 pairs + edge cases passed");
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError(expected + " != " + actual);
    }
}
