package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.KeyBindings;
import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.platform.Services;

public class MovementInputHandler {

    public static boolean shouldPreventMovement() {
        return ClientGrabTracker.isHoldingGrab && 
               KeyBindings.ROTATE_KEY.isDown() && 
               CommonConfig.CLIENT.preventMovementWhileRotating;
    }
}