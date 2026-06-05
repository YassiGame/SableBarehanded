package dev.juaanp.sablebarehanded.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final KeyMapping ROTATE_KEY = new KeyMapping(
            "key.sable-barehanded.rotate",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.sable-barehanded"
    );

    public static final KeyMapping PIVOT_KEY = new KeyMapping(
            "key.sable-barehanded.pivot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.sable-barehanded"
    );

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return;

        long window = mc.getWindow().getWindow();
        KeyMapping[] ourKeys = { ROTATE_KEY, PIVOT_KEY };

        for (KeyMapping ourKey : ourKeys) {

            InputConstants.Key key = InputConstants.getKey(ourKey.saveString());

            if (key != InputConstants.UNKNOWN) {
                boolean isPhysicallyDown = false;

                if (key.getType() == InputConstants.Type.MOUSE) {
                    isPhysicallyDown = GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
                } else if (key.getType() == InputConstants.Type.KEYSYM) {
                    isPhysicallyDown = GLFW.glfwGetKey(window, key.getValue()) == GLFW.GLFW_PRESS;
                }

                ourKey.setDown(isPhysicallyDown);

                for (KeyMapping mapping : mc.options.keyMappings) {
                    if (mapping != ourKey && mapping.same(ourKey)) {
                        mapping.setDown(isPhysicallyDown);
                    }
                }
            }
        }
    }
}