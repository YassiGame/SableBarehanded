package dev.juaanp.sablebarehanded.handler;

import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3d;

public class InteractionHandler {

    public static boolean shouldCancelBlockDestroy(Player player) {
        if (ClientGrabTracker.isHoldingGrab) return true;
        
        Minecraft mc = Minecraft.getInstance();
        return player != null && player.getMainHandItem().isEmpty() && mc.options.keyUse.isDown() && isTargetingSubLevel(mc);
    }

    public static InteractionResult handleItemUse(Player player, InteractionHand hand) {
        if (ClientGrabTracker.isHoldingGrab) {
            return InteractionResult.FAIL;
        }

        if (hand == InteractionHand.OFF_HAND && player.getMainHandItem().isEmpty() && isTargetingSubLevel(Minecraft.getInstance())) {
            return InteractionResult.PASS;
        }

        return null;
    }

    public static boolean shouldCancelEntityInteraction() {
        return ClientGrabTracker.isHoldingGrab;
    }

    private static boolean isTargetingSubLevel(Minecraft mc) {
        if (mc.level == null) return false;
        
        if (mc.hitResult instanceof BlockHitResult blockHit && mc.hitResult.getType() != HitResult.Type.MISS) {
            BlockPos pos = blockHit.getBlockPos();
            Vector3d blockCenter = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            return Sable.HELPER.getContaining(mc.level, blockCenter) != null;
        }
        return false;
    }
}