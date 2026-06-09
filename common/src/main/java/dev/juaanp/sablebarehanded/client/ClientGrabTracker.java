package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.mixin.accesor.MultiPlayerGameModeAccessor;
import dev.juaanp.sablebarehanded.physics.GrabPhysicsManager;
import dev.juaanp.sablebarehanded.platform.Services;
import dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class ClientGrabTracker {
    public static boolean isHoldingGrab = false;
    public static boolean isPulling = false;
    public static double pendingYaw = 0.0;
    public static double pendingPitch = 0.0;
    public static int assemblyChargeTicks = 0;
    public static BlockPos assemblyTargetPos = null;
    public static int currentRequiredAssemblyTicks = 20;
    public static double initialAssemblyDistance = 0.0;
    public static double grabbedMass = 0.0;
    public static java.util.UUID grabbedSubLevelId = null;
    public static org.joml.Vector3d localGrabPivot = null;
    public static double grabRestDistance = 0.0;
    public static double currentTetherStrain = 0.0;
    public static boolean isWaitingForGrabSync = false;
    public static float smoothPullIntensity = 0.0F;
    public static boolean smoothPullIntensityInitialized = false;
    public static boolean preventRegrabUntilRelease = false;
    public static boolean wasHoldingGrabLastTick = false;
    public static int keysReleasedTicks = 0;
    public static boolean pendingStopGrab = false;

    public static void resetAssemblyCharge() {
        assemblyChargeTicks = 0;
        assemblyTargetPos = null;
        initialAssemblyDistance = 0.0;
        isPulling = false;
        currentTetherStrain = 0.0;
        smoothPullIntensity = 0.0F;
        smoothPullIntensityInitialized = false;
    }

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (isHoldingGrab) {
            mc.player.yBodyRot = mc.player.yHeadRot;
            mc.player.yBodyRotO = mc.player.yHeadRotO;

            if (CommonConfig.COMMON.enablePhysicalTether && grabbedSubLevelId != null && localGrabPivot != null
                    && !mc.player.isCreative() && !mc.player.isSpectator()) {
                dev.ryanhcode.sable.api.sublevel.SubLevelContainer container = dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(mc.level);
                if (container != null) {
                    dev.ryanhcode.sable.sublevel.SubLevel subLevel = container.getSubLevel(grabbedSubLevelId);

                    if (subLevel != null && !subLevel.isRemoved()) {
                        org.joml.Vector3d actualGlobalPosJoml = subLevel.logicalPose().transformPosition(new org.joml.Vector3d(localGrabPivot));
                        Vec3 actualPos = new Vec3(actualGlobalPosJoml.x, actualGlobalPosJoml.y, actualGlobalPosJoml.z);

                        Vec3 eyePos = mc.player.getEyePosition();
                        double currentDist = eyePos.distanceTo(actualPos);
                        double armStretchTolerance = CommonConfig.COMMON.armStretchTolerance;

                        double stretchBeyondRest = currentDist - grabRestDistance;
                        if (stretchBeyondRest > armStretchTolerance) {
                            double activeStretch = stretchBeyondRest - armStretchTolerance;
                            currentTetherStrain = Mth.clamp(activeStretch / 2.0, 0.0, 1.0);
                        } else {
                            currentTetherStrain *= 0.85;
                        }
                    } else {
                        isHoldingGrab = false;
                        resetGrabState();
                    }
                }
            }
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

        if (!bothDown) {
            keysReleasedTicks++;
        } else {
            keysReleasedTicks = 0;
        }

        if (keysReleasedTicks >= CommonConfig.CLIENT.regrabDebounceTicks) {
            preventRegrabUntilRelease = false;
        }

        if (bothDown && !isHoldingGrab && mc.player.getMainHandItem().isEmpty() && !preventRegrabUntilRelease) {

            if (assemblyTargetPos == null) {
                double reach = GrabPhysicsManager.getGrabReach(mc.player);
                HitResult hit = mc.player.pick(reach, 0.0f, false);

                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockPos currentPos = blockHit.getBlockPos();
                    Vec3 blockCenter = Vec3.atCenterOf(currentPos);
                    double distanceToHit = mc.player.getEyePosition().distanceTo(blockCenter);
                    BlockState state = mc.level.getBlockState(currentPos);
                    boolean isIgnored = AssemblyBehaviorHelper.isIgnored(mc.level, currentPos, state);
                    Vector3d hitPos = new Vector3d(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);

                    boolean preventDueToMining = false;
                    if (CommonConfig.CLIENT.preventAssemblyWhenMining && mc.gameMode != null) {
                        float miningProgress = ((MultiPlayerGameModeAccessor) mc.gameMode).getDestroyProgress();
                        if (miningProgress > CommonConfig.CLIENT.barehandedAssemblyMiningThreshold) preventDueToMining = true;
                    }

                    if (Sable.HELPER.getContaining(mc.level, hitPos) != null) {
                        Services.NETWORK.sendRequestGrab(currentPos);
                        isHoldingGrab = true;
                        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
                        resetAssemblyCharge();

                    } else if (isSneaking && CommonConfig.COMMON.enableBarehandedAssembly && distanceToHit <= CommonConfig.COMMON.barehandedAssemblyMaxDistance && !isIgnored && !preventDueToMining) {

                        assemblyTargetPos = currentPos;
                        assemblyChargeTicks = 1;
                        isPulling = false;
                        initialAssemblyDistance = distanceToHit;

                        var blocksToAssemble = AssemblyBehaviorHelper.getConnectedBlocks(mc.level, currentPos);
                        currentRequiredAssemblyTicks = AssemblyBehaviorHelper.calculateAssemblyTicks(mc.player, mc.level, blocksToAssemble);

                        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
                    }
                }
            } else {
                if (!isSneaking) {
                    resetAssemblyCharge();
                    return;
                }

                Vec3 targetCenter = Vec3.atCenterOf(assemblyTargetPos);
                Vec3 playerEyePos = mc.player.getEyePosition();
                double currentDist = playerEyePos.distanceTo(targetCenter);

                if (currentDist > CommonConfig.COMMON.barehandedAssemblyMaxDistance + CommonConfig.COMMON.assemblyClientDistanceTolerance) {
                    resetAssemblyCharge();
                    return;
                }

                double stretch = currentDist - initialAssemblyDistance;
                boolean requiresPulling = currentRequiredAssemblyTicks > 2;

                float targetPull = 0.0F;
                boolean shouldAdvanceCharge = false;

                if (!requiresPulling) {
                    targetPull = 1.0F;
                    shouldAdvanceCharge = true;
                } else {
                    if (stretch > CommonConfig.COMMON.pullThreshold) {
                        targetPull = 1.0F;
                        shouldAdvanceCharge = true;
                    } else if (stretch > 0.05) {
                        targetPull = (float) (stretch / CommonConfig.COMMON.pullThreshold);
                        shouldAdvanceCharge = true;
                    } else {
                        targetPull = 0.0F;
                        shouldAdvanceCharge = false;
                    }
                }

                if (!smoothPullIntensityInitialized) {
                    smoothPullIntensity = targetPull;
                    smoothPullIntensityInitialized = true;
                } else {
                    smoothPullIntensity += (targetPull - smoothPullIntensity) * 0.15F;
                }
                isPulling = smoothPullIntensity > 0.05F;

                if (shouldAdvanceCharge) {
                    assemblyChargeTicks++;
                }

                if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();

                if (assemblyChargeTicks >= currentRequiredAssemblyTicks) {
                    Services.NETWORK.sendAssembleGrabRequest(assemblyTargetPos);
                    isHoldingGrab = true;
                    isWaitingForGrabSync = true;
                    currentTetherStrain = 0.0;
                    resetAssemblyCharge();
                }
            }
        } else if (!bothDown && isHoldingGrab) {
            if (isWaitingForGrabSync) {
                pendingStopGrab = true;
            } else {
                isHoldingGrab = false;
                Services.NETWORK.sendStopGrabbingRequest();
                resetAssemblyCharge();
            }
        } else if (!isHoldingGrab) {
            resetAssemblyCharge();
        }

        if (wasHoldingGrabLastTick && !isHoldingGrab && bothDown) {
            preventRegrabUntilRelease = true;
            keysReleasedTicks = 0;
        }
        wasHoldingGrabLastTick = isHoldingGrab;
    }

    public static void renderSableOverlay(GuiGraphics graphics) {
        if (!isHoldingGrab) return;
        if (!CommonConfig.COMMON.enableRotation) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        String rotateKey = KeyBindings.ROTATE_KEY.getTranslatedKeyMessage().getString().toUpperCase();
        String pivotKey = KeyBindings.PIVOT_KEY.getTranslatedKeyMessage().getString().toUpperCase();

        int textY = screenHeight - 65;
        int hintY = textY - 14;

        if (!KeyBindings.ROTATE_KEY.isDown()) {
            String hint = "Hold [ " + rotateKey + " ] to rotate ";
            int width = mc.font.width(hint);
            graphics.drawString(mc.font, hint, (screenWidth - width) / 2, textY, 0xAAAAAA, true);
            return;
        }

        boolean isKeyDown = KeyBindings.PIVOT_KEY.isDown();
        boolean isCenter = CommonConfig.CLIENT.rotateAroundCenter ^ isKeyDown;

        String text = "Rotation Pivot: " + (isCenter ? "CENTER OF MASS " : "GRAB POINT ");
        int color = isCenter ? 0x55FF55 : 0xFFAA00;

        int textWidth = mc.font.width(text);

        int boxLeft = (screenWidth - textWidth) / 2 - 10;
        int boxRight = (screenWidth + textWidth) / 2 + 10;
        int boxTop = textY - 4;
        int boxBottom = textY + mc.font.lineHeight + 4;

        graphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x88000000);
        graphics.drawString(mc.font, text, (screenWidth - textWidth) / 2, textY, color, true);

        String action = isKeyDown ? "Release " : "Hold ";
        String target = isCenter ? "Grab Point " : "Center Mass ";
        String hint = action + " [ " + pivotKey + " ] for " + target;

        int hintWidth = mc.font.width(hint);
        graphics.drawString(mc.font, hint, (screenWidth - hintWidth) / 2, hintY, 0xAAAAAA, true);
    }

    public static boolean shouldCancelInteraction() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        if (isHoldingGrab || assemblyTargetPos != null) return true;

        boolean bothDown = mc.options.keyAttack.isDown() && mc.options.keyUse.isDown();
        boolean isSneaking = mc.player.isShiftKeyDown();

        if (bothDown && isSneaking && CommonConfig.COMMON.enableBarehandedAssembly && mc.player.getMainHandItem().isEmpty()) {

            if (CommonConfig.CLIENT.preventAssemblyWhenMining && mc.gameMode != null) {
                float miningProgress = ((MultiPlayerGameModeAccessor) mc.gameMode).getDestroyProgress();
                if (miningProgress > CommonConfig.CLIENT.barehandedAssemblyMiningThreshold) {
                    return false;
                }
            }

            double reach = GrabPhysicsManager.getGrabReach(mc.player);
            HitResult hit = mc.player.pick(reach, 0.0f, false);

            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos currentPos = blockHit.getBlockPos();

                Vec3 blockCenter = Vec3.atCenterOf(currentPos);
                double distanceToHit = mc.player.getEyePosition().distanceTo(blockCenter);

                BlockState targetState = mc.level.getBlockState(currentPos);

                if (distanceToHit <= CommonConfig.COMMON.barehandedAssemblyMaxDistance && !AssemblyBehaviorHelper.isIgnored(mc.level, currentPos, targetState)) {
                    Vector3d hitPos = new Vector3d(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);
                    if (Sable.HELPER.getContaining(mc.level, hitPos) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double getEncumbranceRatio(net.minecraft.world.entity.player.Player player) {
        if (!CommonConfig.COMMON.enableEncumbrance || !isHoldingGrab) return 0.0;

        if (player.isCreative() || player.isSpectator()) return 0.0;

        if (isWaitingForGrabSync && grabbedMass <= 0.0) return 1.0;

        if (grabbedMass <= 0.0) return 0.0;

        double strengthMultiplier = 1.0;
        if (player.hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST)) {
            int amplifier = player.getEffect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST).getAmplifier();
            strengthMultiplier = amplifier == 0 ? CommonConfig.COMMON.strength1Multiplier : CommonConfig.COMMON.strength2Multiplier;
        }

        double maxCapacity = CommonConfig.COMMON.maxForce * strengthMultiplier;
        double objectWeight = grabbedMass * CommonConfig.COMMON.physicsGravity;
        double rawRatio = objectWeight / maxCapacity;

        return Math.min(Math.pow(rawRatio, 2.0), 1.0);
    }

    public static double getEffectiveEncumbranceRatio(net.minecraft.world.entity.player.Player player) {
        if (assemblyTargetPos != null) return 1.0;

        if (isHoldingGrab) {
            if (isWaitingForGrabSync && grabbedMass <= 0.0) return 1.0;

            return getEncumbranceRatio(player);
        }

        return 0.0;
    }

    public static double getCameraRestrictionRatio(net.minecraft.world.entity.player.Player player) {
        double encumbrance = getEffectiveEncumbranceRatio(player);
        if (encumbrance <= 0.0) return 0.0;
        return Math.max(encumbrance, currentTetherStrain) * CommonConfig.COMMON.maxCameraPenalty;
    }

    public static void resetGrabState() {
        grabbedMass = 0.0;
        grabbedSubLevelId = null;
        localGrabPivot = null;
        grabRestDistance = 0.0;
        currentTetherStrain = 0.0;
        isHoldingGrab = false;
        isWaitingForGrabSync = false;
        pendingStopGrab = false;
    }

    public static Vec3 getCurrentObjectPosition() {
        if (!isHoldingGrab || grabbedSubLevelId == null || localGrabPivot == null) return null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        dev.ryanhcode.sable.api.sublevel.SubLevelContainer container = dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(mc.level);
        if (container != null) {
            dev.ryanhcode.sable.sublevel.SubLevel subLevel = container.getSubLevel(grabbedSubLevelId);
            if (subLevel != null && !subLevel.isRemoved()) {
                org.joml.Vector3d actualGlobalPosJoml = subLevel.logicalPose().transformPosition(new org.joml.Vector3d(localGrabPivot));
                return new Vec3(actualGlobalPosJoml.x, actualGlobalPosJoml.y, actualGlobalPosJoml.z);
            }
        }
        return null;
    }
}