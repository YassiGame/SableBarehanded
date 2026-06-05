package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.platform.Services;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
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
}