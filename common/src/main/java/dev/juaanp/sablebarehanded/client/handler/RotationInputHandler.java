package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.KeyBindings;
import dev.juaanp.sablebarehanded.config.CommonConfig;

public class RotationInputHandler {

    public static boolean handleRotation(double dx, double dy) {
        if (ClientGrabTracker.isHoldingGrab && KeyBindings.ROTATE_KEY.isDown() && CommonConfig.COMMON.enableRotation) {

            if (dx != 0.0 || dy != 0.0) {
                double hSens = CommonConfig.CLIENT.horizontalRotationSensitivity * 0.01;
                double vSens = CommonConfig.CLIENT.verticalRotationSensitivity * 0.01;

                double yaw   = -dx * hSens;
                double pitch = dy * vSens;

                if (CommonConfig.CLIENT.invertHorizontalRotation) yaw = -yaw;
                if (CommonConfig.CLIENT.invertVerticalRotation)   pitch = -pitch;

                ClientGrabTracker.pendingYaw   += yaw;
                ClientGrabTracker.pendingPitch += pitch;
            }

            return true;
        }
        return false;
    }
}