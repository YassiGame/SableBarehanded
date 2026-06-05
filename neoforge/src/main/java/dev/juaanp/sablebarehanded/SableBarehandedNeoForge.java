package dev.juaanp.sablebarehanded;

import dev.juaanp.sablebarehanded.config.NeoForgeGrabConfig; // Asegúrate de que el import sea el correcto
import dev.juaanp.sablebarehanded.network.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class SableBarehandedNeoForge {

    public SableBarehandedNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeGrabConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, NeoForgeGrabConfig.CLIENT_SPEC);
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);

        registrar.playToServer(RequestGrabPacket.TYPE, RequestGrabPacket.CODEC, NeoForgePacketHandlers::handleRequestGrab);
        registrar.playToServer(StopGrabbingPacket.TYPE, StopGrabbingPacket.CODEC, NeoForgePacketHandlers::handleStopGrabbing);
        registrar.playToServer(RotateGrabPacket.TYPE, RotateGrabPacket.CODEC, NeoForgePacketHandlers::handleRotateGrab);

        registrar.playToClient(StartGrabbingAnimationPacket.TYPE, StartGrabbingAnimationPacket.CODEC, NeoForgePacketHandlers::handleStartAnim);
        registrar.playToClient(StopGrabbingAnimationPacket.TYPE, StopGrabbingAnimationPacket.CODEC, NeoForgePacketHandlers::handleStopAnim);
        registrar.playToClient(SyncGhostStatePacket.TYPE, SyncGhostStatePacket.CODEC, NeoForgePacketHandlers::handleGhostStateSync);
    }
}