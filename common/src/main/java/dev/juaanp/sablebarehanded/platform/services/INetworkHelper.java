package dev.juaanp.sablebarehanded.platform.services;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import java.util.UUID;

public interface INetworkHelper {
    void sendStartGrabbingAnimation(Player player);
    void sendStopGrabbingAnimation(Player player);
    void sendRequestGrab(BlockPos pos);
    void sendStopGrabbingRequest();
    void sendRotateGrab(double deltaX, double deltaY, boolean rotateAroundCenter);
    void sendGhostStateSync(ServerSubLevel subLevel, UUID grabberId, byte collisionMask);
}