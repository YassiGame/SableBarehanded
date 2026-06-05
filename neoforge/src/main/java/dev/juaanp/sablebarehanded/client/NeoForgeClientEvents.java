package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.Constants;
import dev.juaanp.sablebarehanded.platform.Services;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;

        ClientGrabTracker.clientTick();
        KeyBindings.clientTick();

        if (ClientGrabTracker.pendingYaw != 0.0 || ClientGrabTracker.pendingPitch != 0.0) {
            Services.NETWORK.sendRotateGrab(ClientGrabTracker.pendingYaw, ClientGrabTracker.pendingPitch);

            ClientGrabTracker.pendingYaw = 0.0;
            ClientGrabTracker.pendingPitch = 0.0;
        }
    }

    @SubscribeEvent
    public static void onInteractKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (ClientGrabTracker.isHoldingGrab) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.ROTATE_KEY);
    }
}