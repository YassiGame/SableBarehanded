package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientAssemblyTracker;
import dev.juaanp.sablebarehanded.client.ClientGrabSession;
import dev.juaanp.sablebarehanded.config.ServerConfig;
import dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3d;

public class ClientInteractionHandler {

    public static boolean shouldCancelBlockDestroy(Player player) {
        if (ClientGrabSession.isHoldingGrab) return true;

        Minecraft mc = Minecraft.getInstance();
        return player != null && player.getMainHandItem().isEmpty() && mc.options.keyUse.isDown() && isTargetingSubLevel(mc);
    }

    public static InteractionResult handleItemUse(Player player, InteractionHand hand) {
        if (ClientGrabSession.isHoldingGrab) {
            return InteractionResult.FAIL;
        }

        if (hand == InteractionHand.OFF_HAND && player.getMainHandItem().isEmpty() && isTargetingSubLevel(Minecraft.getInstance())) {
            return InteractionResult.PASS;
        }

        return null;
    }

    public static boolean shouldCancelEntityInteraction() {
        return ClientGrabSession.isHoldingGrab;
    }

    public static boolean shouldCancelInteraction() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        if (ClientGrabSession.isHoldingGrab || ClientAssemblyTracker.isActive()) return true;

        boolean bothDown = mc.options.keyAttack.isDown() && mc.options.keyUse.isDown();
        boolean isSneaking = mc.player.isShiftKeyDown();

        if (bothDown && isSneaking && ServerConfig.INSTANCE.enableBarehandedAssembly && mc.player.getMainHandItem().isEmpty()) {
            HitResult hit = mc.player.pick(ServerConfig.INSTANCE.barehandedAssemblyMaxDistance, 0.0f, false);

            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos currentPos = blockHit.getBlockPos();
                BlockState targetState = mc.level.getBlockState(currentPos);

                if (!AssemblyBehaviorHelper.isIgnored(mc.level, currentPos, targetState)) {
                    Vector3d hitPos = new Vector3d(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);
                    if (Sable.HELPER.getContaining(mc.level, hitPos) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
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