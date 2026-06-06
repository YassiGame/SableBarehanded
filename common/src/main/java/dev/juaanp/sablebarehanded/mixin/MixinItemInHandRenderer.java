package dev.juaanp.sablebarehanded.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.ClientPayloadHandler;
import dev.juaanp.sablebarehanded.client.handler.RenderAnimationHandler;
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

    @Unique private static final float TRANSITION_SPEED = 0.2F;
    @Unique private static final float SHAKE_MULTIPLIER = 0.04F;

    @Unique private float transitionProgress = 0.0F;
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

        if (isGrabbing) {
            this.transitionProgress += TRANSITION_SPEED;
        } else {
            this.transitionProgress -= TRANSITION_SPEED;
        }
        this.transitionProgress = Mth.clamp(this.transitionProgress, 0.0F, 1.0F);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        float t = Mth.lerp(partialTicks, this.oldTransitionProgress, this.transitionProgress);

        int charge = ClientGrabTracker.assemblyChargeTicks;

        if (charge > 0 && ClientGrabTracker.isPulling && stack.isEmpty()) {

            float maxTicks = Math.max(1.0F, (float) ClientGrabTracker.currentRequiredAssemblyTicks);
            float progress = Math.min((float) charge / maxTicks, 1.0F);

            float shakeIntensity = progress * SHAKE_MULTIPLIER;

            float time = player.tickCount + partialTicks;
            poseStack.translate(
                    Mth.sin(time * 3.0F) * shakeIntensity,
                    Mth.cos(time * 4.0F) * shakeIntensity,
                    Mth.sin(time * 5.0F) * shakeIntensity
            );
        }

        if (t <= 0.0F || player.isInvisible()) return;

        float ease = calculateSmoothStep(t);

        RenderAnimationHandler.renderGrabArm(player, hand, equippedProgress, stack, poseStack, buffer, combinedLight, this.entityRenderDispatcher, ease);

        if (stack.isEmpty() || ease >= 0.99F) {
            ci.cancel();
        } else {
            poseStack.pushPose();
            poseStack.translate(0.0D, -ease * 1.5D, 0.0D);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void onRenderArmWithItemReturn(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        float t = Mth.lerp(partialTicks, this.oldTransitionProgress, this.transitionProgress);
        if (t > 0.0F && !player.isInvisible()) {
            float ease = calculateSmoothStep(t);
            if (!stack.isEmpty() && ease < 0.99F) {
                poseStack.popPose();
            }
        }
    }
}