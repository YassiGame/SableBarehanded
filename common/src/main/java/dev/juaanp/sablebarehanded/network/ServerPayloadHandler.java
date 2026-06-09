package dev.juaanp.sablebarehanded.network;

import dev.juaanp.sablebarehanded.physics.GrabActionHandler;
import dev.juaanp.sablebarehanded.physics.GrabRotationController;
import dev.juaanp.sablebarehanded.physics.ServerGrabManager;
import net.minecraft.server.level.ServerPlayer;

public class ServerPayloadHandler {

    public static void handleRequestGrab(ServerPlayer player, RequestGrabPacket packet) {
        GrabActionHandler.startGrabbing(player, packet.blockPos());
    }

    public static void handleAssembleGrab(ServerPlayer player, AssembleGrabPacket packet) {
        GrabActionHandler.assembleAndGrab(player, packet.blockPos());
    }

    public static void handleStopGrabbing(ServerPlayer player, StopGrabbingPacket packet) {
        ServerGrabManager.stopGrabbing(player.getUUID());
    }

    public static void handleRotateGrab(ServerPlayer player, RotateGrabPacket packet) {
        GrabRotationController.applyRotation(player, packet.deltaX(), packet.deltaY(), packet.rotateAroundCenter());
    }
}