package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.network.StartGrabbingAnimationPacket;
import dev.juaanp.sablebarehanded.network.StopGrabbingAnimationPacket;
import dev.juaanp.sablebarehanded.network.SyncGhostStatePacket;
import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import dev.juaanp.sablebarehanded.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SableBarehandedFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(KeyBindings.ROTATE_KEY);

        ClientPlayNetworking.registerGlobalReceiver(StartGrabbingAnimationPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> ClientPayloadHandler.handleStartGrabbingAnimation(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(StopGrabbingAnimationPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> ClientPayloadHandler.handleStopGrabbingAnimation(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncGhostStatePacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                GrabPhysicsManager.setClientGhostState(payload.subLevelId(), payload.grabberId(), payload.collisionMask());
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            ClientGrabTracker.clientTick();
            KeyBindings.clientTick();

            if (ClientGrabTracker.pendingYaw != 0.0 || ClientGrabTracker.pendingPitch != 0.0) {
                Services.NETWORK.sendRotateGrab(ClientGrabTracker.pendingYaw, ClientGrabTracker.pendingPitch);

                ClientGrabTracker.pendingYaw = 0.0;
                ClientGrabTracker.pendingPitch = 0.0;
            }
        });
    }
}