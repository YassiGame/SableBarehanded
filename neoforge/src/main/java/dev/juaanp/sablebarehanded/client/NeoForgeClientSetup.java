package dev.juaanp.sablebarehanded.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class NeoForgeClientSetup {

    public static void init(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> BarehandedConfigScreen.create(parent));
        modEventBus.addListener(NeoForgeClientSetup::registerKeyMappings);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.ROTATE_KEY);
        event.register(KeyBindings.PIVOT_KEY);
    }
}