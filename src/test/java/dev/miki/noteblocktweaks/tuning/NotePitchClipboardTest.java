/*
 * File: NotePitchClipboardTest.java
 * Modification: 2026-10-10 - Offline clipboard boundary/overwrite tests.
 */
package dev.miki.noteblocktweaks.tuning;

public final class NotePitchClipboardTest {
    public static void main(String[] args) {
        NotePitchClipboard clipboard = new NotePitchClipboard();
        if (clipboard.hasPitch()) throw new AssertionError("Clipboard should start empty");
        clipboard.copy(0);
        if (clipboard.getPitch() != 0) throw new AssertionError("Expected pitch 0");
        clipboard.copy(24);
        if (clipboard.getPitch() != 24) throw new AssertionError("Expected pitch 24");
        try { clipboard.copy(25); throw new AssertionError("Invalid pitch accepted"); }
        catch (IllegalArgumentException expected) { /* expected */ }
        clipboard.clear();
        if (clipboard.hasPitch()) throw new AssertionError("Clipboard should be empty");
        System.out.println("Clipboard tests passed");
    }
}
