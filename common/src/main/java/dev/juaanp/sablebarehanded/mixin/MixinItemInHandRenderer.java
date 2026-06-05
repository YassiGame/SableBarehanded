package dev.juaanp.sablebarehanded.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.juaanp.sablebarehanded.client.ClientPayloadHandler;
import dev.juaanp.sablebarehanded.client.handler.RenderAnimationHandler;
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

    @Unique private float SableBarehandedTransition = 0.0F;
    @Unique private float oSableBarehandedTransition = 0.0F;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        this.oSableBarehandedTransition = this.SableBarehandedTransition;
        boolean isGrabbing = this.minecraft.player != null && ClientPayloadHandler.GRABBING_PLAYERS.contains(this.minecraft.player.getUUID());

        if (isGrabbing) {
            this.SableBarehandedTransition += 0.2F;
        } else {
            this.SableBarehandedTransition -= 0.2F;
        }
        this.SableBarehandedTransition = Mth.clamp(this.SableBarehandedTransition, 0.0F, 1.0F);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        float t = Mth.lerp(partialTicks, this.oSableBarehandedTransition, this.SableBarehandedTransition);
        if (t <= 0.0F || player.isInvisible()) return;

        float ease = t * t * (3.0F - 2.0F * t);

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
        float t = Mth.lerp(partialTicks, this.oSableBarehandedTransition, this.SableBarehandedTransition);
        if (t > 0.0F && !player.isInvisible()) {
            float ease = t * t * (3.0F - 2.0F * t);
            if (!stack.isEmpty() && ease < 0.99F) {
                poseStack.popPose();
            }
        }
    }
}