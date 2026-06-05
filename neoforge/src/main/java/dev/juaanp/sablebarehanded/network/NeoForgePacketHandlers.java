package dev.juaanp.sablebarehanded.network;

import dev.juaanp.sablebarehanded.client.ClientPayloadHandler;
import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NeoForgePacketHandlers {

    public static void handleRequestGrab(RequestGrabPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                GrabPhysicsManager.startGrabbing(context.player(), packet.blockPos());
            }
        });
    }

    public static void handleStopGrabbing(StopGrabbingPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                GrabPhysicsManager.stopGrabbing(context.player().getUUID());
            }
        });
    }

    public static void handleStartAnim(StartGrabbingAnimationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                ClientPayloadHandler.handleStartGrabbingAnimation(packet);
            }
        });
    }

    public static void handleStopAnim(StopGrabbingAnimationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                ClientPayloadHandler.handleStopGrabbingAnimation(packet);
            }
        });
    }

    public static void handleRotateGrab(RotateGrabPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                GrabPhysicsManager.applyRotation(context.player(), packet.deltaX(), packet.deltaY());
            }
        });
    }

    public static void handleGhostStateSync(SyncGhostStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                GrabPhysicsManager.setClientGhostState(packet.subLevelId(), packet.grabberId(), packet.collisionMask());
            }
        });
    }
}