package dev.juaanp.sablebarehanded.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderAnimationHandler {

    public static void renderGrabArm(
            AbstractClientPlayer player, InteractionHand hand,
            float equippedProgress, ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, EntityRenderDispatcher dispatcher, float ease) {

        boolean isMainHand = (hand == InteractionHand.MAIN_HAND);
        boolean isVanillaVisible = isMainHand || !stack.isEmpty();

        // =================================================================
        // LA LÓGICA DEL "WEAPON SWAP" (Cambio de Arma Seguro)
        // Si tienes un ítem, o si es tu mano secundaria vacía (que no debería existir),
        // activamos el hundimiento del brazo para que desaparezca sin parpadeos.
        // =================================================================
        boolean needsSwapOffset = !stack.isEmpty() || !isVanillaVisible;

        HumanoidArm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean isRight = (arm == HumanoidArm.RIGHT);
        float side = isRight ? 1.0F : -1.0F;

        // 1. MATRIZ VANILLA
        PoseStack vanillaStack = new PoseStack();
        vanillaStack.translate(side * 0.64F, -0.6F + equippedProgress * -0.6F, -0.72F);
        vanillaStack.mulPose(Axis.YP.rotationDegrees(side * 45.0F));

        vanillaStack.translate(side * -1.0F, 3.6F, 3.5F);
        vanillaStack.mulPose(Axis.ZP.rotationDegrees(side * 120.0F));
        vanillaStack.mulPose(Axis.XP.rotationDegrees(200.0F));
        vanillaStack.mulPose(Axis.YP.rotationDegrees(side * -135.0F));
        vanillaStack.translate(side * 5.6F, 0.0F, 0.0F);

        Matrix4f matVanilla = vanillaStack.last().pose();

        // 2. MATRIZ CUSTOM (Tu Pose)
        PoseStack customStack = new PoseStack();
        customStack.translate(side * 0.18F, -0.6F + equippedProgress * -0.6F, -0.2F);
        customStack.mulPose(Axis.XP.rotationDegrees(-60.0F));
        customStack.mulPose(Axis.YP.rotationDegrees(side * 5.0F));
        customStack.mulPose(Axis.ZP.rotationDegrees(side * 15.0F));
        customStack.mulPose(Axis.YP.rotationDegrees(side * 200.0F));

        Matrix4f matCustom = customStack.last().pose();

        // 3. SLERP (Interpolación Esférica de vectores)
        Vector3f transVanilla = matVanilla.getTranslation(new Vector3f());
        Vector3f transCustom  = matCustom.getTranslation(new Vector3f());

        Quaternionf rotVanilla = matVanilla.getNormalizedRotation(new Quaternionf());
        Quaternionf rotCustom  = matCustom.getNormalizedRotation(new Quaternionf());

        Vector3f lerpedTrans = transVanilla.lerp(transCustom, ease);
        Quaternionf slerpedRot = rotVanilla.slerp(rotCustom, ease);

        // =================================================================
        // CÁLCULO DE ALTURA DE OCULTAMIENTO
        // =================================================================
        float customArmYOffset = 0.0F;
        if (needsSwapOffset) {
            // Empuja el brazo desnudo hasta -1.5 bloques por debajo de la cámara
            // al terminar la animación, para que deje espacio al ítem real que sube.
            customArmYOffset = -(1.0F - ease) * 1.5F;
        }

        float dip = (float) Math.sin(ease * Math.PI);

        // =================================================================
        // APLICAR A LA PANTALLA
        // =================================================================
        poseStack.pushPose();

        // Sumamos el hundimiento por ítem (customArmYOffset) al hundimiento por peso (dip)
        poseStack.translate(lerpedTrans.x(), lerpedTrans.y() - (0.8F * dip) + customArmYOffset, lerpedTrans.z());
        poseStack.mulPose(Axis.XP.rotationDegrees(50.0F * dip));
        poseStack.mulPose(slerpedRot);

        PlayerRenderer playerRenderer = (PlayerRenderer) dispatcher.getRenderer(player);
        if (isRight) {
            playerRenderer.renderRightHand(poseStack, buffer, combinedLight, player);
        } else {
            playerRenderer.renderLeftHand(poseStack, buffer, combinedLight, player);
        }

        poseStack.popPose();
    }
}