package dev.juaanp.sablebarehanded.api;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class SableBarehandedEvents {

    private static final List<BiPredicate<Player, ServerSubLevel>> BEFORE_GRAB_LISTENERS = new ArrayList<>();
    private static final List<BiConsumer<Player, ServerSubLevel>> ON_GRAB_LISTENERS = new ArrayList<>();
    private static final List<BiConsumer<Player, ServerSubLevel>> ON_RELEASE_LISTENERS = new ArrayList<>();

    public static void onBeforeGrab(BiPredicate<Player, ServerSubLevel> listener) {
        BEFORE_GRAB_LISTENERS.add(listener);
    }

    public static void onGrab(BiConsumer<Player, ServerSubLevel> listener) {
        ON_GRAB_LISTENERS.add(listener);
    }

    public static void onRelease(BiConsumer<Player, ServerSubLevel> listener) {
        ON_RELEASE_LISTENERS.add(listener);
    }

    public static boolean fireBeforeGrab(Player player, ServerSubLevel subLevel) {
        for (BiPredicate<Player, ServerSubLevel> listener : BEFORE_GRAB_LISTENERS) {
            if (!listener.test(player, subLevel)) {
                return false;
            }
        }
        return true;
    }

    public static void fireOnGrab(Player player, ServerSubLevel subLevel) {
        ON_GRAB_LISTENERS.forEach(listener -> listener.accept(player, subLevel));
    }

    public static void fireOnRelease(Player player, ServerSubLevel subLevel) {
        ON_RELEASE_LISTENERS.forEach(listener -> listener.accept(player, subLevel));
    }
}