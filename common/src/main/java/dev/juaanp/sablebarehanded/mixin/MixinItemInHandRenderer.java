package dev.juaanp.sablebarehanded.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.ClientPayloadHandler;
import dev.juaanp.sablebarehanded.client.handler.RenderAnimationHandler;
import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow @Final private Minecraft minecraft;

    @Unique private float transitionProgress    = 0.0F;
    @Unique private float oldTransitionProgress = 0.0F;

    @Unique
    private float calculateSmoothStep(float t) {
        return t * t * (3.0F - 2.0F * t);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        this.oldTransitionProgress = this.transitionProgress;

        boolean isGrabbing = this.minecraft.player != null && (
                ClientPayloadHandler.GRABBING_PLAYERS.contains(this.minecraft.player.getUUID()) ||
                        ClientGrabTracker.isHoldingGrab ||
                        ClientGrabTracker.assemblyChargeTicks > 0
        );

        float speed = (float) CommonConfig.CLIENT.armTransitionSpeed;
        if (isGrabbing) {
            this.transitionProgress += speed;
        } else {
            this.transitionProgress -= speed;
        }
        this.transitionProgress = Mth.clamp(this.transitionProgress, 0.0F, 1.0F);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(
            AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack poseStack,
            MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {

        float t = Mth.lerp(partialTicks, this.oldTransitionProgress, this.transitionProgress);

        int charge = ClientGrabTracker.assemblyChargeTicks;

        if (charge > 0 && ClientGrabTracker.isPulling && stack.isEmpty()) {
            float maxTicks = Math.max(1.0F, (float) ClientGrabTracker.currentRequiredAssemblyTicks);
            float progress = Math.min((float) charge / maxTicks, 1.0F);

            float visualThreshold = (float) CommonConfig.CLIENT.visualShakeThreshold;
            if (progress >= visualThreshold) {
                float shakeIntensity = progress * (float) CommonConfig.CLIENT.assemblyShakeMultiplier;
                float time = player.tickCount + partialTicks;
                poseStack.translate(
                        Mth.sin(time * (float) CommonConfig.CLIENT.shakeFrequencyX) * shakeIntensity,
                        Mth.cos(time * (float) CommonConfig.CLIENT.shakeFrequencyY) * shakeIntensity,
                        Mth.sin(time * (float) CommonConfig.CLIENT.shakeFrequencyZ) * shakeIntensity
                );
            }
        }

        if (t <= 0.0F || player.isInvisible()) return;

        float ease = calculateSmoothStep(t);
        float easeFullThreshold = (float) CommonConfig.CLIENT.armEaseFullThreshold;

        RenderAnimationHandler.renderGrabArm(
                player, hand, equippedProgress, stack,
                poseStack, buffer, combinedLight, this.entityRenderDispatcher, ease);

        if (stack.isEmpty() || ease >= easeFullThreshold) {
            ci.cancel();
        } else {
            poseStack.pushPose();
            poseStack.translate(0.0D, -ease * CommonConfig.CLIENT.armGrabLowerOffset, 0.0D);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void onRenderArmWithItemReturn(
            AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack poseStack,
            MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {

        float t = Mth.lerp(partialTicks, this.oldTransitionProgress, this.transitionProgress);
        if (t > 0.0F && !player.isInvisible()) {
            float ease = calculateSmoothStep(t);
            float easeFullThreshold = (float) CommonConfig.CLIENT.armEaseFullThreshold;
            if (!stack.isEmpty() && ease < easeFullThreshold) {
                poseStack.popPose();
            }
        }
    }
}