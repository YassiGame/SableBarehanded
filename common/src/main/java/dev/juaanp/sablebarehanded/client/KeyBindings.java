package dev.juaanp.sablebarehanded.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class KeyBindings {
    public static final KeyMapping ROTATE_KEY = new KeyMapping(
            "key.sable-barehanded.rotate",
            InputConstants.Type.KEYSYM,
            org.lwjgl.glfw.GLFW.GLFW_KEY_R,
            "key.categories.sable-barehanded"
    );

    public static float currentChargeProgress = 0f;
    public static boolean isRenderActive = false;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (ClientGrabTracker.isHoldingGrab) {
            currentChargeProgress = 0f;
            isRenderActive = false;
        }
    }
}