package dev.juaanp.sablebarehanded.client.handler;

import dev.juaanp.sablebarehanded.client.ClientPayloadHandler;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ThirdPersonAnimationHandler {

    public static void applyGrabPose(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart rightSleeve, ModelPart leftSleeve, float swimAmount) {
        if (entity instanceof Player player && ClientPayloadHandler.GRABBING_PLAYERS.contains(player.getUUID())) {

            player.yBodyRot = player.yHeadRot;
            player.yBodyRotO = player.yHeadRotO;

            float targetRotX = (float) Math.toRadians(-90.0);
            float targetRotY = (float) Math.toRadians(0.0);
            float targetRotZ = (float) Math.toRadians(0.0);

            if (swimAmount > 0.0F) {
                targetRotX += swimAmount * ((float) Math.PI / 2F);
            }

            rightArm.xRot = targetRotX;
            rightArm.yRot = targetRotY;
            rightArm.zRot = targetRotZ;

            leftArm.xRot = targetRotX;
            leftArm.yRot = targetRotY;
            leftArm.zRot = targetRotZ;

            rightSleeve.copyFrom(rightArm);
            leftSleeve.copyFrom(leftArm);
        }
    }
}