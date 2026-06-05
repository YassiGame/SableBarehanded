package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.KeyBindings;
import dev.juaanp.sablebarehanded.platform.Services;

public class RotationInputHandler {

    public static boolean handleRotation(double dx, double dy) {
        if (ClientGrabTracker.isHoldingGrab && KeyBindings.ROTATE_KEY.isDown() && Services.CONFIG.enableRotation()) {
            
            if (dx != 0.0 || dy != 0.0) {
                double sens  = Services.CONFIG.rotationSensitivity() * 0.01;
                double yaw   = -dx * sens;
                double pitch = dy * sens;

                if (Services.CONFIG.invertRotation()) {
                    yaw   = -yaw;
                    pitch = -pitch;
                }

                ClientGrabTracker.pendingYaw   += yaw;
                ClientGrabTracker.pendingPitch += pitch;
            }
            
            return true;
        }
        return false;
    }
}