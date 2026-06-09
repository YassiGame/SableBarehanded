package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabSession;
import dev.juaanp.sablebarehanded.client.KeyBindings;
import dev.juaanp.sablebarehanded.config.ClientConfig;

public class MovementInputHandler {

    public static boolean shouldPreventMovement() {
        return ClientGrabSession.isHoldingGrab && 
               KeyBindings.ROTATE_KEY.isDown() && 
               ClientConfig.INSTANCE.preventMovementWhileRotating;
    }
}