package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.ClientConfig;

public class ClientInputTracker {
    public static boolean preventRegrabUntilRelease = false;
    public static boolean wasHoldingGrabLastTick = false;
    public static int keysReleasedTicks = 0;

    public static double pendingYaw = 0.0;
    public static double pendingPitch = 0.0;

    public static void tickDebounce(boolean bothDown) {
        if (!bothDown) {
            keysReleasedTicks++;
        } else {
            keysReleasedTicks = 0;
        }

        if (keysReleasedTicks >= ClientConfig.INSTANCE.regrabDebounceTicks) {
            preventRegrabUntilRelease = false;
        }

        if (wasHoldingGrabLastTick && !ClientGrabSession.isHoldingGrab && bothDown) {
            preventRegrabUntilRelease = true;
            keysReleasedTicks = 0;
        }
        wasHoldingGrabLastTick = ClientGrabSession.isHoldingGrab;
    }

    public static boolean canInitiateGrab() {
        return !preventRegrabUntilRelease;
    }
}