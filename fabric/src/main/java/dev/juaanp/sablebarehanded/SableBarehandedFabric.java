package dev.juaanp.sablebarehanded;

import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.network.*;
import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class SableBarehandedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonConfig.load();

        PayloadTypeRegistry.playC2S().register(RequestGrabPacket.TYPE, RequestGrabPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AssembleGrabPacket.TYPE, AssembleGrabPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(StopGrabbingPacket.TYPE, StopGrabbingPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(RotateGrabPacket.TYPE, RotateGrabPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(StartGrabbingAnimationPacket.TYPE, StartGrabbingAnimationPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(StopGrabbingAnimationPacket.TYPE, StopGrabbingAnimationPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncGhostStatePacket.TYPE, SyncGhostStatePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncConfigPacket.TYPE, SyncConfigPacket.CODEC); // Nuevo paquete

        ServerPlayNetworking.registerGlobalReceiver(RequestGrabPacket.TYPE, (payload, context) -> {
            context.server().execute(() -> GrabPhysicsManager.startGrabbing(context.player(), payload.blockPos()));
        });

        ServerPlayNetworking.registerGlobalReceiver(AssembleGrabPacket.TYPE, (payload, context) -> {
            context.server().execute(() -> GrabPhysicsManager.assembleAndGrab(context.player(), payload.blockPos()));
        });

        ServerPlayNetworking.registerGlobalReceiver(StopGrabbingPacket.TYPE, (payload, context) -> {
            context.server().execute(() -> GrabPhysicsManager.stopGrabbing(context.player().getUUID()));
        });

        ServerPlayNetworking.registerGlobalReceiver(RotateGrabPacket.TYPE, (payload, context) -> {
            context.server().execute(() -> GrabPhysicsManager.applyRotation(context.player(), payload.deltaX(), payload.deltaY(), payload.rotateAroundCenter()));
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                GrabPhysicsManager.tickPlayer(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            SyncConfigPacket syncPacket = new SyncConfigPacket(CommonConfig.getCommonJson());
            ServerPlayNetworking.send(handler.getPlayer(), syncPacket);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            GrabPhysicsManager.onPlayerLoggedOut(handler.getPlayer());
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof Player player) {
                GrabPhysicsManager.onPlayerDeath(player);
            }
        });
    }
}