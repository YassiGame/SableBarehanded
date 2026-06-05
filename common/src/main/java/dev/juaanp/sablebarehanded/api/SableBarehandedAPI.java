package dev.juaanp.sablebarehanded.api;

import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SableBarehandedAPI {

    public static boolean isPlayerGrabbing(Player player) {
        return GrabPhysicsManager.isPlayerGrabbing(player);
    }

    public static void forceDrop(Player player) {
        GrabPhysicsManager.stopGrabbing(player.getUUID());
    }

    public static void forceGrab(Player player, BlockPos targetPos) {
        GrabPhysicsManager.startGrabbing(player, targetPos);
    }

    @Nullable
    public static ServerSubLevel getGrabbedSubLevel(Player player) {
        return GrabPhysicsManager.getGrabbedSubLevel(player);
    }
}