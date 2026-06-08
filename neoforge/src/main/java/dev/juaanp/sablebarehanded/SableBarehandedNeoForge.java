package dev.juaanp.sablebarehanded;

import dev.juaanp.sablebarehanded.client.NeoForgeClientSetup;
import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.network.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class SableBarehandedNeoForge {

    public SableBarehandedNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        CommonConfig.load();

        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        if (FMLEnvironment.dist.isClient()) {
            NeoForgeClientSetup.registerConfigScreen(modContainer);
        }
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SyncConfigPacket packet = new SyncConfigPacket(CommonConfig.getCommonJson());
            PacketDistributor.sendToPlayer(serverPlayer, packet);
        }
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);

        registrar.playToServer(RequestGrabPacket.TYPE, RequestGrabPacket.CODEC, NeoForgePacketHandlers::handleRequestGrab);
        registrar.playToServer(AssembleGrabPacket.TYPE, AssembleGrabPacket.CODEC, NeoForgePacketHandlers::handleAssembleGrab);
        registrar.playToServer(StopGrabbingPacket.TYPE, StopGrabbingPacket.CODEC, NeoForgePacketHandlers::handleStopGrabbing);
        registrar.playToServer(RotateGrabPacket.TYPE, RotateGrabPacket.CODEC, NeoForgePacketHandlers::handleRotateGrab);

        registrar.playToClient(StartGrabbingAnimationPacket.TYPE, StartGrabbingAnimationPacket.CODEC, NeoForgePacketHandlers::handleStartAnim);
        registrar.playToClient(StopGrabbingAnimationPacket.TYPE, StopGrabbingAnimationPacket.CODEC, NeoForgePacketHandlers::handleStopAnim);
        registrar.playToClient(SyncGhostStatePacket.TYPE, SyncGhostStatePacket.CODEC, NeoForgePacketHandlers::handleGhostStateSync);

        registrar.playToClient(SyncConfigPacket.TYPE, SyncConfigPacket.CODEC, NeoForgePacketHandlers::handleConfigSync);
        registrar.playToClient(SyncGrabStatePacket.TYPE, SyncGrabStatePacket.CODEC, NeoForgePacketHandlers::handleSyncGrabState);
    }
}