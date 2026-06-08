package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.network.*;
import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import dev.juaanp.sablebarehanded.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public class FabricClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(KeyBindings.ROTATE_KEY);
        KeyBindingHelper.registerKeyBinding(KeyBindings.PIVOT_KEY);

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

        ClientPlayNetworking.registerGlobalReceiver(SyncConfigPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                CommonConfig.loadCommonFromJson(payload.configJson());
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            CommonConfig.load();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            ClientGrabTracker.clientTick();
            KeyBindings.clientTick();

            boolean isRotateKeyDown = KeyBindings.ROTATE_KEY.isDown();

            if (isRotateKeyDown || ClientGrabTracker.pendingYaw != 0.0 || ClientGrabTracker.pendingPitch != 0.0) {
                boolean useCenter = CommonConfig.CLIENT.rotateAroundCenter ^ KeyBindings.PIVOT_KEY.isDown();
                Services.NETWORK.sendRotateGrab(ClientGrabTracker.pendingYaw, ClientGrabTracker.pendingPitch, useCenter);

                ClientGrabTracker.pendingYaw = 0.0;
                ClientGrabTracker.pendingPitch = 0.0;
            }
        });

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            ClientGrabTracker.renderSableOverlay(graphics);
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClientSide() && ClientGrabTracker.shouldCancelInteraction()) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide() && ClientGrabTracker.shouldCancelInteraction()) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
                SyncGrabStatePacket.TYPE,
                (payload, context) -> context.client().execute(() ->
                        dev.juaanp.sablebarehanded.client.ClientPayloadHandler.handleSyncGrabState(payload)
                )
        );
    }
}