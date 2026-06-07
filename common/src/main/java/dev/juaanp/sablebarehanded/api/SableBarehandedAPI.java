package dev.juaanp.sablebarehanded.api;

import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Sable Barehanded API
 * Allows other mods to interact with the physics, grabbing, and assembly mechanics.
 */
public class SableBarehandedAPI {

    /**
     * Checks if the player is currently holding a physical object.
     */
    public static boolean isPlayerGrabbing(Player player) {
        return GrabPhysicsManager.isPlayerGrabbing(player);
    }

    /**
     * Checks if the player is currently holding a specific SubLevel.
     */
    public static boolean isHoldingSubLevel(Player player, ServerSubLevel subLevel) {
        return GrabPhysicsManager.isHoldingSubLevel(player, subLevel);
    }

    /**
     * Returns the SubLevel the player is currently holding, or null if their hands are empty.
     */
    @Nullable
    public static ServerSubLevel getGrabbedSubLevel(Player player) {
        return GrabPhysicsManager.getGrabbedSubLevel(player);
    }

    /**
     * Gets the player's dynamic interaction reach, taking into account the base
     * attribute, the configuration bonus, and the Creative Super Strength mode.
     */
    public static double getGrabReach(Player player) {
        return GrabPhysicsManager.getGrabReach(player);
    }

    /**
     * Forces the player to immediately drop whatever they are holding.
     */
    public static void forceDrop(Player player) {
        GrabPhysicsManager.stopGrabbing(player.getUUID());
    }

    /**
     * Forces the player to grab a block that already belongs to an existing physical SubLevel.
     * Fails silently if the block is not a physical object or is out of reach.
     */
    public static void forceGrab(Player player, BlockPos targetPos) {
        GrabPhysicsManager.startGrabbing(player, targetPos);
    }

    /**
     * Forces the player to evaluate a world block, assemble its connected blocks,
     * convert them into a physical object (SubLevel), and grab it immediately.
     */
    public static void forceAssembleAndGrab(Player player, BlockPos targetPos) {
        GrabPhysicsManager.assembleAndGrab(player, targetPos);
    }

    /**
     * Applies programmatic rotation to the object the player is currently holding.
     *
     * @param yaw Horizontal axis movement.
     * @param pitch Vertical axis movement.
     * @param rotateAroundCenter If true, rotates from the center of mass; if false, from the grab point.
     */
    public static void applyRotation(Player player, double yaw, double pitch, boolean rotateAroundCenter) {
        GrabPhysicsManager.applyRotation(player, yaw, pitch, rotateAroundCenter);
    }
}