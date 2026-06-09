package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.ClientConfig;
import dev.juaanp.sablebarehanded.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ClientHudRenderer {

    public static void renderSableOverlay(GuiGraphics graphics) {
        if (!ClientGrabSession.isHoldingGrab) return;
        if (!ServerConfig.INSTANCE.enableRotation) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        String rotateKey = KeyBindings.ROTATE_KEY.getTranslatedKeyMessage().getString().toUpperCase();
        String pivotKey = KeyBindings.PIVOT_KEY.getTranslatedKeyMessage().getString().toUpperCase();

        int textY = screenHeight - 65;
        int hintY = textY - 14;

        if (!KeyBindings.ROTATE_KEY.isDown()) {
            String hint = "Hold [ " + rotateKey + " ] to rotate ";
            int width = mc.font.width(hint);
            graphics.drawString(mc.font, hint, (screenWidth - width) / 2, textY, 0xAAAAAA, true);
            return;
        }

        boolean isKeyDown = KeyBindings.PIVOT_KEY.isDown();
        boolean isCenter = ClientConfig.INSTANCE.rotateAroundCenter ^ isKeyDown;

        String text = "Rotation Pivot: " + (isCenter ? "CENTER OF MASS " : "GRAB POINT ");
        int color = isCenter ? 0x55FF55 : 0xFFAA00;

        int textWidth = mc.font.width(text);

        int boxLeft = (screenWidth - textWidth) / 2 - 10;
        int boxRight = (screenWidth + textWidth) / 2 + 10;
        int boxTop = textY - 4;
        int boxBottom = textY + mc.font.lineHeight + 4;

        graphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x88000000);
        graphics.drawString(mc.font, text, (screenWidth - textWidth) / 2, textY, color, true);

        String action = isKeyDown ? "Release " : "Hold ";
        String target = isCenter ? "Grab Point " : "Center Mass ";
        String hint = action + "[ " + pivotKey + " ] for " + target;

        int hintWidth = mc.font.width(hint);
        graphics.drawString(mc.font, hint, (screenWidth - hintWidth) / 2, hintY, 0xAAAAAA, true);
    }
}