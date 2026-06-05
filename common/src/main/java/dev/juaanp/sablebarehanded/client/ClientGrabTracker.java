package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.platform.Services;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3d;

public class ClientGrabTracker {
    public static boolean isHoldingGrab = false;

    public static double pendingYaw = 0.0;
    public static double pendingPitch = 0.0;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (isHoldingGrab) {
            mc.player.yBodyRot = mc.player.yHeadRot;
            mc.player.yBodyRotO = mc.player.yHeadRotO;
        }

        if (mc.screen != null) {
            if (isHoldingGrab) {
                isHoldingGrab = false;
                Services.NETWORK.sendStopGrabbingRequest();
            }
            return;
        }

        boolean isAttackDown = mc.options.keyAttack.isDown();
        boolean isUseDown = mc.options.keyUse.isDown();
        boolean bothDown = isAttackDown && isUseDown;

        if (bothDown && !isHoldingGrab && mc.player.getMainHandItem().isEmpty()) {
            double reach = mc.player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
            HitResult hit = mc.player.pick(reach, 0.0f, false);

            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                Vector3d hitPos = new Vector3d(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);

                if (Sable.HELPER.getContaining(mc.level, hitPos) != null) {
                    Services.NETWORK.sendRequestGrab(blockHit.getBlockPos());
                    isHoldingGrab = true;

                    if (mc.gameMode != null) {
                        mc.gameMode.stopDestroyBlock();
                    }
                }
            }
        } else if (!bothDown && isHoldingGrab) {
            isHoldingGrab = false;
            Services.NETWORK.sendStopGrabbingRequest();
        }
    }

    public static void renderSableOverlay(GuiGraphics graphics) {
        if (!isHoldingGrab) return;
        if (!Services.CONFIG.enableRotation()) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        String rotateKey = KeyBindings.ROTATE_KEY.getTranslatedKeyMessage().getString().toUpperCase();
        String pivotKey = KeyBindings.PIVOT_KEY.getTranslatedKeyMessage().getString().toUpperCase();

        int textY = screenHeight - 65;
        int hintY = textY - 14;

        if (!KeyBindings.ROTATE_KEY.isDown()) {
            String hint = "Hold [" + rotateKey + "] to rotate";
            int width = mc.font.width(hint);
            graphics.drawString(mc.font, hint, (screenWidth - width) / 2, textY, 0xAAAAAA, true);
            return;
        }

        boolean isKeyDown = KeyBindings.PIVOT_KEY.isDown();
        boolean isCenter = Services.CONFIG.rotateAroundCenter() ^ isKeyDown;

        String text = "Rotation Pivot: " + (isCenter ? "CENTER OF MASS" : "GRAB POINT");
        int color = isCenter ? 0x55FF55 : 0xFFAA00;

        int textWidth = mc.font.width(text);

        int boxLeft = (screenWidth - textWidth) / 2 - 10;
        int boxRight = (screenWidth + textWidth) / 2 + 10;
        int boxTop = textY - 4;
        int boxBottom = textY + mc.font.lineHeight + 4;

        graphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x88000000);
        graphics.drawString(mc.font, text, (screenWidth - textWidth) / 2, textY, color, true);

        String action = isKeyDown ? "Release" : "Hold";
        String target = isCenter ? "Grab Point" : "Center Mass";
        String hint = action + " [" + pivotKey + "] for " + target;

        int hintWidth = mc.font.width(hint);
        graphics.drawString(mc.font, hint, (screenWidth - hintWidth) / 2, hintY, 0xAAAAAA, true);
    }
}