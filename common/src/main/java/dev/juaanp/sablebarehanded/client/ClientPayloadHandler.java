package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.network.StartGrabbingAnimationPacket;
import dev.juaanp.sablebarehanded.network.StopGrabbingAnimationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientPayloadHandler {

    public static final Set<UUID> GRABBING_PLAYERS = new HashSet<>();

    public static void handleStartGrabbingAnimation(StartGrabbingAnimationPacket packet) {
        Player player = getPlayerFromId(packet.entityId());
        if (player != null) {
            GRABBING_PLAYERS.add(player.getUUID());
        }
    }

    public static void handleStopGrabbingAnimation(StopGrabbingAnimationPacket packet) {
        Player player = getPlayerFromId(packet.entityId());
        if (player != null) {
            GRABBING_PLAYERS.remove(player.getUUID());

            if (player == Minecraft.getInstance().player) {
                ClientGrabTracker.isHoldingGrab = false;
            }
        }
    }

    private static Player getPlayerFromId(int entityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(entityId);
            if (entity instanceof Player player) {
                return player;
            }
        }
        return null;
    }
}