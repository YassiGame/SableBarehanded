package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabSession;
import dev.juaanp.sablebarehanded.client.ClientInputTracker;
import dev.juaanp.sablebarehanded.client.KeyBindings;
import dev.juaanp.sablebarehanded.config.ClientConfig;
import dev.juaanp.sablebarehanded.config.ServerConfig;

public class RotationInputHandler {

    public static boolean handleRotation(double dx, double dy) {
        if (ClientGrabSession.isHoldingGrab && KeyBindings.ROTATE_KEY.isDown() && ServerConfig.INSTANCE.enableRotation) {

            if (dx != 0.0 || dy != 0.0) {
                double hSens = ClientConfig.INSTANCE.horizontalRotationSensitivity * 0.01;
                double vSens = ClientConfig.INSTANCE.verticalRotationSensitivity * 0.01;

                double yaw   = -dx * hSens;
                double pitch = dy * vSens;

                if (ClientConfig.INSTANCE.invertHorizontalRotation) yaw = -yaw;
                if (ClientConfig.INSTANCE.invertVerticalRotation)   pitch = -pitch;

                ClientInputTracker.pendingYaw   += yaw;
                ClientInputTracker.pendingPitch += pitch;
            }

            return true;
        }
        return false;
    }
}