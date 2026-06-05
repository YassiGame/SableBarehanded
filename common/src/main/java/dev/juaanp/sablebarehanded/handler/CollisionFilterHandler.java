package dev.juaanp.sablebarehanded.handler;

import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;

public class CollisionFilterHandler {

    private static final ThreadLocal<Entity> CURRENT_COLLIDING_ENTITY = new ThreadLocal<>();

    public static void setProcessingEntity(Entity entity) {
        CURRENT_COLLIDING_ENTITY.set(entity);
    }

    public static void clearProcessingEntity() {
        CURRENT_COLLIDING_ENTITY.remove();
    }

    public static boolean shouldFilterSubLevel(SubLevel subLevel) {
        Entity entity = CURRENT_COLLIDING_ENTITY.get();
        return entity != null && GrabPhysicsManager.shouldIgnoreEntityCollision(subLevel, entity);
    }
}