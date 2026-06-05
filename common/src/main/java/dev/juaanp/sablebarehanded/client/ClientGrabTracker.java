package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.mixin.accesor.MultiPlayerGameModeAccessor;
import dev.juaanp.sablebarehanded.platform.Services;
import dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class ClientGrabTracker {
    public static boolean isHoldingGrab = false;

    public static double pendingYaw = 0.0;
    public static double pendingPitch = 0.0;
    public static int assemblyChargeTicks = 0;
    public static BlockPos assemblyTargetPos = null;

    public static int currentRequiredAssemblyTicks = 20;

    public static void resetAssemblyCharge() {
        assemblyChargeTicks = 0;
        assemblyTargetPos = null;
    }

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (isHoldingGrab) {
            mc.player.yBodyRot = mc.player.yHeadRot;
            mc.player.yBodyRotO = mc.player.yHeadRotO;
        }

        if (mc.screen != null) {
            if (isHoldingGrab) {
                isHoldingGrab = false;
                Services.NETWORK.sendStopGrabbingRequest();
            }
            resetAssemblyCharge();
            return;
        }

        boolean isAttackDown = mc.options.keyAttack.isDown();
        boolean isUseDown = mc.options.keyUse.isDown();
        boolean bothDown = isAttackDown && isUseDown;
        boolean isSneaking = mc.player.isShiftKeyDown();

        if (bothDown && !isHoldingGrab && mc.player.getMainHandItem().isEmpty()) {
            double reach = mc.player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE).getValue();
            net.minecraft.world.phys.HitResult hit = mc.player.pick(reach, 0.0f, false);

            if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                net.minecraft.world.phys.BlockHitResult blockHit = (net.minecraft.world.phys.BlockHitResult) hit;
                net.minecraft.core.BlockPos currentPos = blockHit.getBlockPos();

                net.minecraft.world.phys.Vec3 blockCenter = net.minecraft.world.phys.Vec3.atCenterOf(currentPos);
                double distanceToHit = mc.player.getEyePosition().distanceTo(blockCenter);

                net.minecraft.world.level.block.state.BlockState state = mc.level.getBlockState(currentPos);
                boolean isIgnored = dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper.isIgnored(mc.level, currentPos, state);

                Vector3d hitPos = new Vector3d(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);

                boolean preventDueToMining = false;
                if (Services.CONFIG.preventAssemblyWhenMining() && mc.gameMode != null) {
                    float miningProgress = ((MultiPlayerGameModeAccessor) mc.gameMode).getDestroyProgress();
                    if (miningProgress > Services.CONFIG.barehandedAssemblyMiningThreshold()) {
                        preventDueToMining = true;
                    }
                }

                if (Sable.HELPER.getContaining(mc.level, hitPos) != null) {
                    Services.NETWORK.sendRequestGrab(currentPos);
                    isHoldingGrab = true;
                    if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
                    resetAssemblyCharge();

                } else if (isSneaking && Services.CONFIG.enableBarehandedAssembly() && distanceToHit <= Services.CONFIG.barehandedAssemblyMaxDistance() && !isIgnored && !preventDueToMining) {

                    if (assemblyTargetPos == null || !assemblyTargetPos.equals(currentPos)) {
                        assemblyTargetPos = currentPos;
                        assemblyChargeTicks = 1;

                        boolean isCreativeSuper = mc.player.isCreative() && Services.CONFIG.creativeSuperStrength();

                        if (isCreativeSuper) {
                            currentRequiredAssemblyTicks = 1;
                        }
                        else if (dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper.isFastLift(mc.level, currentPos, state)) {
                            currentRequiredAssemblyTicks = 2;
                        } else {
                            float progressPerTick = state.getDestroyProgress(mc.player, mc.level, currentPos);

                            if (progressPerTick <= 0.0F) {
                                currentRequiredAssemblyTicks = Integer.MAX_VALUE;
                            } else {
                                int vanillaTicks = (int) Math.ceil(1.0F / progressPerTick);
                                double strengthMulti = 1.0;
                                var strengthEffect = mc.player.getEffect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST);
                                if (strengthEffect != null) {
                                    int amp = strengthEffect.getAmplifier();
                                    strengthMulti = amp == 0 ? Services.CONFIG.strength1Multiplier() : Services.CONFIG.strength2Multiplier();
                                }
                                currentRequiredAssemblyTicks = (int) Math.max(1, (vanillaTicks / strengthMulti) / Services.CONFIG.barehandedAssemblySpeedMultiplier());
                            }
                        }
                    } else {
                        assemblyChargeTicks++;
                    }

                    if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();

                    if (assemblyChargeTicks >= currentRequiredAssemblyTicks) {
                        Services.NETWORK.sendAssembleGrabRequest(assemblyTargetPos);
                        isHoldingGrab = true;
                        resetAssemblyCharge();
                    }

                } else {
                    resetAssemblyCharge();
                }
            } else {
                resetAssemblyCharge();
            }
        } else if (!bothDown && isHoldingGrab) {
            isHoldingGrab = false;
            Services.NETWORK.sendStopGrabbingRequest();
            resetAssemblyCharge();
        } else {
            resetAssemblyCharge();
        }
    }

    public static void renderSableOverlay(GuiGraphics graphics) {
        if (!isHoldingGrab) return;
        if (!Services.CONFIG.enableRotation()) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        String rotateKey = KeyBindings.ROTATE_KEY.getTranslatedKeyMessage().getString().toUpperCase();
        String pivotKey = KeyBindings.PIVOT_KEY.getTranslatedKeyMessage().getString().toUpperCase();

        int textY = screenHeight - 65;
        int hintY = textY - 14;

        if (!KeyBindings.ROTATE_KEY.isDown()) {
            String hint = "Hold [" + rotateKey + "] to rotate";
            int width = mc.font.width(hint);
            graphics.drawString(mc.font, hint, (screenWidth - width) / 2, textY, 0xAAAAAA, true);
            return;
        }

        boolean isKeyDown = KeyBindings.PIVOT_KEY.isDown();
        boolean isCenter = Services.CONFIG.rotateAroundCenter() ^ isKeyDown;

        String text = "Rotation Pivot: " + (isCenter ? "CENTER OF MASS" : "GRAB POINT");
        int color = isCenter ? 0x55FF55 : 0xFFAA00;

        int textWidth = mc.font.width(text);

        int boxLeft = (screenWidth - textWidth) / 2 - 10;
        int boxRight = (screenWidth + textWidth) / 2 + 10;
        int boxTop = textY - 4;
        int boxBottom = textY + mc.font.lineHeight + 4;

        graphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x88000000);
        graphics.drawString(mc.font, text, (screenWidth - textWidth) / 2, textY, color, true);

        String action = isKeyDown ? "Release" : "Hold";
        String target = isCenter ? "Grab Point" : "Center Mass";
        String hint = action + " [" + pivotKey + "] for " + target;

        int hintWidth = mc.font.width(hint);
        graphics.drawString(mc.font, hint, (screenWidth - hintWidth) / 2, hintY, 0xAAAAAA, true);
    }

    public static boolean shouldCancelInteraction() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        if (isHoldingGrab || assemblyTargetPos != null) return true;

        boolean bothDown = mc.options.keyAttack.isDown() && mc.options.keyUse.isDown();
        boolean isSneaking = mc.player.isShiftKeyDown();

        if (bothDown && isSneaking && Services.CONFIG.enableBarehandedAssembly() && mc.player.getMainHandItem().isEmpty()) {

            if (Services.CONFIG.preventAssemblyWhenMining() && mc.gameMode != null) {
                float miningProgress = ((MultiPlayerGameModeAccessor) mc.gameMode).getDestroyProgress();
                if (miningProgress > Services.CONFIG.barehandedAssemblyMiningThreshold()) {
                    return false;
                }
            }

            double reach = mc.player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
            HitResult hit = mc.player.pick(reach, 0.0f, false);

            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos currentPos = blockHit.getBlockPos();

                Vec3 blockCenter = Vec3.atCenterOf(currentPos);
                double distanceToHit = mc.player.getEyePosition().distanceTo(blockCenter);
                boolean isUnbreakable = mc.level.getBlockState(currentPos).getDestroySpeed(mc.level, currentPos) < 0.0F;

                BlockState targetState = mc.level.getBlockState(currentPos);

                if (distanceToHit <= Services.CONFIG.barehandedAssemblyMaxDistance() && !AssemblyBehaviorHelper.isIgnored(mc.level, currentPos, targetState)) {
                    Vector3d hitPos = new Vector3d(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);
                    if (Sable.HELPER.getContaining(mc.level, hitPos) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}