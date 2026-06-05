package dev.juaanp.sablebarehanded.platform;

import dev.juaanp.sablebarehanded.network.*;
import dev.juaanp.sablebarehanded.platform.services.INetworkHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class FabricNetworkHelper implements INetworkHelper {

    @Override
    public void sendStartGrabbingAnimation(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            StartGrabbingAnimationPacket packet = new StartGrabbingAnimationPacket(player.getId());
            for (ServerPlayer tracking : PlayerLookup.tracking(serverPlayer)) {
                ServerPlayNetworking.send(tracking, packet);
            }
            ServerPlayNetworking.send(serverPlayer, packet);
        }
    }

    @Override
    public void sendStopGrabbingAnimation(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            StopGrabbingAnimationPacket packet = new StopGrabbingAnimationPacket(player.getId());
            for (ServerPlayer tracking : PlayerLookup.tracking(serverPlayer)) {
                ServerPlayNetworking.send(tracking, packet);
            }
            ServerPlayNetworking.send(serverPlayer, packet);
        }
    }

    @Override
    public void sendRequestGrab(BlockPos pos) {
        ClientPlayNetworking.send(new RequestGrabPacket(pos));
    }

    @Override
    public void sendStopGrabbingRequest() {
        ClientPlayNetworking.send(new StopGrabbingPacket());
    }

    @Override
    public void sendRotateGrab(double deltaX, double deltaY, boolean rotateAroundCenter) { // <-- AÑADIDO
        ClientPlayNetworking.send(new RotateGrabPacket(deltaX, deltaY, rotateAroundCenter)); // <-- AÑADIDO
    }

    @Override
    public void sendGhostStateSync(ServerSubLevel subLevel, UUID grabberId, byte collisionMask) {
        SyncGhostStatePacket packet = new SyncGhostStatePacket(subLevel.getUniqueId(), grabberId, collisionMask);
        for (ServerPlayer player : PlayerLookup.world(subLevel.getLevel())) {
            ServerPlayNetworking.send(player, packet);
        }
    }
}