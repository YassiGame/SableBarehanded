package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientAssemblyTracker;
import dev.juaanp.sablebarehanded.client.ClientGrabSession;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public class ClientInteractionHandler {

    public static boolean shouldCancelInteraction() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        if (ClientGrabSession.isHoldingGrab || ClientAssemblyTracker.isActive()) {
            return true;
        }

        boolean bothDown = mc.options.keyAttack.isDown() && mc.options.keyUse.isDown();

        return bothDown && mc.player.getMainHandItem().isEmpty();
    }

    public static InteractionResult handleItemUse(Player player, InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return null;

        if (ClientGrabSession.isHoldingGrab || ClientAssemblyTracker.isActive()) {
            return InteractionResult.FAIL;
        }

        boolean bothDown = mc.options.keyAttack.isDown() && mc.options.keyUse.isDown();

        if (bothDown && player.getMainHandItem().isEmpty()) {
            return InteractionResult.FAIL;
        }

        return null;
    }

    public static boolean shouldCancelEntityInteraction() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        if (ClientGrabSession.isHoldingGrab || ClientAssemblyTracker.isActive()) {
            return true;
        }

        boolean bothDown = mc.options.keyAttack.isDown() && mc.options.keyUse.isDown();
        return bothDown && mc.player.getMainHandItem().isEmpty();
    }
}