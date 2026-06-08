package dev.juaanp.sablebarehanded.mixin;

import dev.juaanp.sablebarehanded.client.ClientGrabTracker;
import dev.juaanp.sablebarehanded.client.handler.MovementInputHandler;
import dev.juaanp.sablebarehanded.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(boolean isMovingSlowly, float slowFactor, CallbackInfo ci) {
        KeyboardInput input = (KeyboardInput) (Object) this;

        if (MovementInputHandler.shouldPreventMovement()) {
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.forwardImpulse = 0.0F;
            input.leftImpulse = 0.0F;
            input.jumping = false;
            input.shiftKeyDown = false;
            return;
        }

        if (Minecraft.getInstance().player != null) {
            double encumbrance = ClientGrabTracker.getEffectiveEncumbranceRatio(Minecraft.getInstance().player);

            if (encumbrance > 0.0) {
                float movementScale = (float) (1.0 - (encumbrance * CommonConfig.COMMON.maxMovementPenalty));

                input.forwardImpulse *= movementScale;
                input.leftImpulse *= movementScale;

                if (encumbrance >= CommonConfig.COMMON.jumpPreventionThreshold) {
                    input.jumping = false;
                }
            }
        }
    }
}