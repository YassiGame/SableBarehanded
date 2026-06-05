package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.KeyBindings;
import dev.juaanp.sablebarehanded.platform.Services;

public class RotationInputHandler {

    public static boolean handleRotation(double dx, double dy) {
        if (ClientGrabTracker.isHoldingGrab && KeyBindings.ROTATE_KEY.isDown() && Services.CONFIG.enableRotation()) {

            if (dx != 0.0 || dy != 0.0) {
                double hSens = Services.CONFIG.horizontalRotationSensitivity() * 0.01;
                double vSens = Services.CONFIG.verticalRotationSensitivity() * 0.01;

                double yaw   = -dx * hSens;
                double pitch = dy * vSens;

                if (Services.CONFIG.invertHorizontalRotation()) yaw = -yaw;
                if (Services.CONFIG.invertVerticalRotation())   pitch = -pitch;

                ClientGrabTracker.pendingYaw   += yaw;
                ClientGrabTracker.pendingPitch += pitch;
            }

            return true;
        }
        return false;
    }
}