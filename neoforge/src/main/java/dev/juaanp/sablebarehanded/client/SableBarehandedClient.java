package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class SableBarehandedClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get().getModContainerById(Constants.MOD_ID).ifPresent(modContainer ->
                modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new)
        );
    }
}