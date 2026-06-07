package dev.juaanp.sablebarehanded.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class NeoForgeClientSetup {

    public static void registerConfigScreen(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> BarehandedConfigScreen.create(parent));
    }
}