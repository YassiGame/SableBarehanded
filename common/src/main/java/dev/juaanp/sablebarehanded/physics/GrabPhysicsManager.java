package dev.juaanp.sablebarehanded.physics;

import dev.juaanp.sablebarehanded.Constants;
import dev.juaanp.sablebarehanded.api.SableBarehandedEvents;
import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.platform.Services;
import dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GrabPhysicsManager {
    private static final ResourceLocation MOVEMENT_PENALTY_ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "grab_movement_penalty");

    private static class GrabSession {
        public final ServerSubLevel subLevel;
        public final float distance;
        public final PhysicsPipeline pipeline;
        public PhysicsConstraintHandle constraintHandle;

        public final Vector3d localPivot;
        public final Vector3d localCenterOfMass;

        public boolean isRotating = false;
        public int rotationTicksLeft = 0;

        public boolean rotateAroundCenter = true;

        public int suspendTicksLeft = 0;

        public byte lastCollisionMask = -1;
        public boolean hasSyncedGhostState = false;

        public final Vector3d anchorGlobalOrigin = new Vector3d();
        public final Quaterniond baseOrientation = new Quaterniond();
        public final Quaterniond targetGlobalOrientation = new Quaterniond();

        public final Vector3d accumulatedPivotOffset = new Vector3d();

        public GrabSession(ServerSubLevel subLevel, float distance, Vector3d localPivot, Vector3d localCenterOfMass,
                           Vector3d initialTarget, Quaterniond initialOrient, PhysicsPipeline pipeline) {
            this.subLevel = subLevel;
            this.distance = distance;
            this.localPivot = localPivot;
            this.localCenterOfMass = localCenterOfMass;
            this.pipeline = pipeline;

            this.anchorGlobalOrigin.set(initialTarget);
            this.baseOrientation.set(initialOrient);
            this.targetGlobalOrientation.set(initialOrient);
        }
    }

    private static final Map<UUID, GrabSession> ACTIVE_GRABS = new HashMap<>();
    private static final Map<UUID, ClientGhostState> CLIENT_GHOST_STATES = new HashMap<>();

    private static class ClientGhostState {
        public final UUID subLevelId;
        public final boolean ignoreEverything;
        public final boolean ignoreSelf;
        public final boolean ignoreOthers;
        public final boolean ignoreEntities;

        public ClientGhostState(UUID subLevelId, byte mask) {
            this.subLevelId = subLevelId;
            this.ignoreEverything = (mask & 1) != 0;
            this.ignoreSelf       = (mask & 2) != 0;
            this.ignoreOthers     = (mask & 4) != 0;
            this.ignoreEntities   = (mask & 8) != 0;
        }
    }

    public static void setClientGhostState(UUID subLevelId, UUID grabberId, byte collisionMask) {
        if (collisionMask == 0) {
            CLIENT_GHOST_STATES.remove(grabberId);
        } else {
            CLIENT_GHOST_STATES.put(grabberId, new ClientGhostState(subLevelId, collisionMask));
        }
    }

    public static boolean isHoldingSubLevel(Player player, ServerSubLevel subLevel) {
        GrabSession grab = ACTIVE_GRABS.get(player.getUUID());
        return grab != null && subLevel.equals(grab.subLevel);
    }

    public static boolean isPlayerGrabbing(Player player) {
        return ACTIVE_GRABS.containsKey(player.getUUID());
    }

    public static ServerSubLevel getGrabbedSubLevel(Player player) {
        GrabSession grab = ACTIVE_GRABS.get(player.getUUID());
        return grab != null ? grab.subLevel : null;
    }

    public static boolean shouldIgnoreEntityCollision(SubLevel subLevel, Entity entity) {
        if (subLevel.getLevel().isClientSide()) {
            for (Map.Entry<UUID, ClientGhostState> entry : CLIENT_GHOST_STATES.entrySet()) {
                ClientGhostState state = entry.getValue();

                if (state.subLevelId.equals(subLevel.getUniqueId())) {
                    UUID grabberId = entry.getKey();

                    if (state.ignoreEverything) return true;

                    if (entity instanceof Player player) {
                        if (player.getUUID().equals(grabberId) ? state.ignoreSelf : state.ignoreOthers) return true;
                    } else {
                        if (state.ignoreEntities) return true;
                    }
                }
            }
            return false;
        }

        for (Map.Entry<UUID, GrabSession> entry : ACTIVE_GRABS.entrySet()) {
            if (entry.getValue().subLevel.equals(subLevel)) {
                UUID grabberId = entry.getKey();
                boolean isRotating = entry.getValue().isRotating;

                boolean ignoreEverything = isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationEverything : CommonConfig.COMMON.ignoreCollisionsGrabEverything;
                if (ignoreEverything) return true;

                if (entity instanceof Player player) {
                    boolean ignoreSelf = isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationSelf : CommonConfig.COMMON.ignoreCollisionsGrabSelf;
                    boolean ignoreOthers = isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationOtherPlayers : CommonConfig.COMMON.ignoreCollisionsGrabOtherPlayers;
                    boolean isSelf = player.getUUID().equals(grabberId);

                    if (isSelf) {
                        // Forzar ignorar colisión si está muy cerca para evitar bucles de empuje (Penetration Resolution Loop)
                        Vector3d blockPos = entry.getValue().subLevel.logicalPose().transformPosition(new Vector3d(entry.getValue().localPivot));
                        double distSq = player.getEyePosition().distanceToSqr(new Vec3(blockPos.x, blockPos.y, blockPos.z));
                        if (distSq < 4.0) { // ~2 bloques
                            return true;
                        }
                    }

                    return isSelf ? ignoreSelf : ignoreOthers;
                } else {
                    return isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationEntities : CommonConfig.COMMON.ignoreCollisionsGrabEntities;
                }
            }
        }
        return false;
    }

    public static void stopGrabbing(UUID playerId) {
        GrabSession session = ACTIVE_GRABS.remove(playerId);
        if (session != null) {
            if (session.constraintHandle != null && !session.subLevel.isRemoved()) {
                session.pipeline.wakeUp(session.subLevel);
                session.constraintHandle.remove();
            }
            Services.NETWORK.sendGhostStateSync(session.subLevel, playerId, (byte) 0);
            Level level = session.subLevel.getLevel();
            if (level != null) {
                Player player = level.getPlayerByUUID(playerId);
                if (player != null) {
                    AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (moveSpeed != null) {
                        moveSpeed.removeModifier(MOVEMENT_PENALTY_ID);
                    }

                    Services.NETWORK.sendStopGrabbingAnimation(player);
                    SableBarehandedEvents.fireOnRelease(player, session.subLevel);
                }
            }
        }
    }

    private static void rebuildConstraint(GrabSession grab) {
        if (grab.constraintHandle != null) {
            grab.constraintHandle.remove();
        }
        grab.constraintHandle = grab.pipeline.addConstraint(
                null, grab.subLevel,
                new FreeConstraintConfiguration(grab.anchorGlobalOrigin, grab.localPivot, grab.baseOrientation)
        );
    }

    private static void performGrab(Player player, ServerSubLevel serverSubLevel, org.joml.Vector3d localGrabBlock) {
        Level level = player.level();
        ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(level);
        if (container == null) return;

        PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

        org.joml.Vector3dc com = serverSubLevel.getMassTracker().getCenterOfMass();
        if (com == null || serverSubLevel.getMassTracker().getMass() <= 0.01) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        Vector3d localCenterOfMass = new Vector3d(com);

        Vector3d globalGrabBlockPos = serverSubLevel.logicalPose().transformPosition(new Vector3d(localGrabBlock));
        float distance = (float) player.getEyePosition().distanceTo(new Vec3(globalGrabBlockPos.x, globalGrabBlockPos.y, globalGrabBlockPos.z));

        Vector3d crosshairTarget = JOMLConversion.toJOML(player.getEyePosition().add(player.getLookAngle().scale(Math.max(CommonConfig.COMMON.minDistance, distance))));
        Quaterniond initialOrient = serverSubLevel.logicalPose().orientation();

        GrabSession session = new GrabSession(serverSubLevel, distance, localGrabBlock, localCenterOfMass, crosshairTarget, initialOrient, pipeline);

        pipeline.wakeUp(serverSubLevel);

        rebuildConstraint(session);
        Services.NETWORK.sendStartGrabbingAnimation(player);
        Services.NETWORK.sendSyncGrabState(player,
                com != null ? serverSubLevel.getMassTracker().getMass() : 0.0,
                serverSubLevel.getUniqueId(),
                localGrabBlock,
                distance
        );
        ACTIVE_GRABS.put(player.getUUID(), session);
    }

    public static void startGrabbing(Player player, BlockPos pos) {
        Level level = player.level();
        if (level.isClientSide() || ACTIVE_GRABS.containsKey(player.getUUID()) || !player.getMainHandItem().isEmpty()) return;

        double reach = GrabPhysicsManager.getGrabReach(player);
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > (reach * reach)) return;

        SubLevelAccess target = Sable.HELPER.getContaining(level, pos);
        if (!(target instanceof ServerSubLevel serverSubLevel)) return;

        org.joml.Vector3d localGrabBlock = new org.joml.Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        performGrab(player, serverSubLevel, localGrabBlock);
        SableBarehandedEvents.fireOnGrab(player, serverSubLevel);
    }

    public static void assembleAndGrab(Player player, BlockPos pos) {
        Level level = player.level();
        if (level.isClientSide() || ACTIVE_GRABS.containsKey(player.getUUID()) || !player.getMainHandItem().isEmpty()) return;

        double reach = GrabPhysicsManager.getGrabReach(player);
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > (reach * reach)) return;

        double maxDist = CommonConfig.COMMON.barehandedAssemblyMaxDistance + 1.0;
        if (player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > (maxDist * maxDist)) return;

        BlockState mainState = level.getBlockState(pos);
        if (AssemblyBehaviorHelper.isIgnored(level, pos, mainState)) return;

        List<BlockPos> blocks = AssemblyBehaviorHelper.getConnectedBlocks(level, pos);

        if (!SableBarehandedEvents.fireBeforeAssemble(player, pos, blocks)) return;

        for (BlockPos bPos : blocks) {
            if (Sable.HELPER.getContaining(level, bPos) != null) return;
        }

        BoundingBox3i bounds = BoundingBox3i.from(blocks);
        SubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks((ServerLevel) level, pos, blocks, bounds);

        if (subLevel instanceof ServerSubLevel serverSubLevel) {

            SableBarehandedEvents.fireOnAssemble(player, serverSubLevel, blocks);

            boolean isFastLift = AssemblyBehaviorHelper.isFastLift(level, pos, mainState);

            if (!isFastLift) {
                net.minecraft.world.level.block.SoundType soundType = mainState.getSoundType();
                level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                level.levelEvent(2001, pos, net.minecraft.world.level.block.Block.getId(mainState));
            } else {
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 0.5f);
            }

            for (BlockPos bPos : blocks) {
                level.updateNeighborsAt(bPos, net.minecraft.world.level.block.Blocks.AIR);
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    BlockPos neighbor = bPos.relative(dir);
                    level.neighborChanged(neighbor, net.minecraft.world.level.block.Blocks.AIR, bPos);

                    net.minecraft.world.level.material.FluidState fluid = level.getFluidState(neighbor);
                    if (!fluid.isEmpty()) {
                        level.scheduleTick(neighbor, fluid.getType(), fluid.getType().getTickDelay(level));
                    }

                    net.minecraft.world.level.block.state.BlockState neighborState = level.getBlockState(neighbor);
                    if (neighborState.getBlock() instanceof net.minecraft.world.level.block.FallingBlock) {
                        level.scheduleTick(neighbor, neighborState.getBlock(), 2);
                    }
                }
            }

            ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(level);

            if (container != null) {
                container.physicsSystem().getPipeline().wakeUp(serverSubLevel);
            }

            Vector3d globalVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vector3d localGrabBlock = serverSubLevel.logicalPose().transformPositionInverse(globalVec, new Vector3d());

            performGrab(player, serverSubLevel, localGrabBlock);
            SableBarehandedEvents.fireOnGrab(player, serverSubLevel);
        }
    }

    public static void applyRotation(Player player, double yaw, double pitch, boolean clientPrefersCenter) {
        if (!CommonConfig.COMMON.enableRotation) return;

        GrabSession grab = ACTIVE_GRABS.get(player.getUUID());
        if (grab == null || grab.subLevel.isRemoved()) return;

        grab.rotateAroundCenter = clientPrefersCenter;

        if (grab.rotationTicksLeft == 0) {
            grab.baseOrientation.set(grab.subLevel.logicalPose().orientation());
            grab.targetGlobalOrientation.set(grab.baseOrientation);
            Vector3d currentActualPivotPos = grab.subLevel.logicalPose().transformPosition(new Vector3d(grab.localPivot));
            grab.anchorGlobalOrigin.set(currentActualPivotPos);
            rebuildConstraint(grab);
        }

        grab.rotationTicksLeft = 5;

        boolean isCreativeSuper = player.isCreative() && CommonConfig.COMMON.creativeSuperStrength;
        double mass = grab.subLevel.getMassTracker().getMass();
        double massFactor = isCreativeSuper ? 1.0 : (1.0 / (1.0 + mass * CommonConfig.COMMON.rotationMassDampingFactor));

        double yawDelta = yaw * massFactor;
        double pitchDelta = pitch * massFactor;

        if (CommonConfig.COMMON.preventFastRotations) {
            yawDelta = Mth.clamp(yawDelta, -CommonConfig.COMMON.maxRotationSpeed, CommonConfig.COMMON.maxRotationSpeed);
            pitchDelta = Mth.clamp(pitchDelta, -CommonConfig.COMMON.maxRotationSpeed, CommonConfig.COMMON.maxRotationSpeed);
        }

        final Vec3 look = player.getLookAngle();
        final Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);

        Vec3 right = look.cross(worldUp);
        double rLen = right.length();
        if (rLen < 1e-6) {
            double yr = Math.toRadians(player.getYRot());
            right = new Vec3(Math.cos(yr), 0.0, Math.sin(yr));
        } else {
            right = right.scale(1.0 / rLen);
        }

        final Vec3 camUp = right.cross(look).normalize();

        final double rx = right.x * pitchDelta + camUp.x * yawDelta;
        final double ry = right.y * pitchDelta + camUp.y * yawDelta;
        final double rz = right.z * pitchDelta + camUp.z * yawDelta;
        final double angle = Math.sqrt(rx * rx + ry * ry + rz * rz);

        if (angle > 1e-10) {
            final double inv = 1.0 / angle;
            grab.targetGlobalOrientation
                    .premul(new Quaterniond(new AxisAngle4d(angle, rx * inv, ry * inv, rz * inv)))
                    .normalize();
        }
    }

    public static void tickPlayer(Player player) {
        UUID playerId = player.getUUID();
        if (player.level().isClientSide()) return;

        Vec3 vel = player.getDeltaMovement();
        if (Math.abs(vel.x) > CommonConfig.COMMON.maxPlayerVelocityXZ || vel.y > CommonConfig.COMMON.maxPlayerVelocityYUp || vel.y < CommonConfig.COMMON.maxPlayerVelocityYDown || Math.abs(vel.z) > CommonConfig.COMMON.maxPlayerVelocityXZ) {
            player.setDeltaMovement(
                    Mth.clamp(vel.x, -CommonConfig.COMMON.maxPlayerVelocityXZ, CommonConfig.COMMON.maxPlayerVelocityXZ),
                    Mth.clamp(vel.y, CommonConfig.COMMON.maxPlayerVelocityYDown, CommonConfig.COMMON.maxPlayerVelocityYUp),
                    Mth.clamp(vel.z, -CommonConfig.COMMON.maxPlayerVelocityXZ, CommonConfig.COMMON.maxPlayerVelocityXZ)
            );
            player.hurtMarked = true;
        }

        GrabSession grab = ACTIVE_GRABS.get(playerId);
        if (grab == null) return;

        ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(player.level());

        if (container == null ||
                grab.subLevel.isRemoved() ||
                container.getSubLevel(grab.subLevel.getUniqueId()) == null ||
                grab.subLevel.getMassTracker().getMass() <= 0.01) {
            stopGrabbing(playerId);
            return;
        }

        if (!player.isAlive()) {
            stopGrabbing(playerId);
            return;
        }

        grab.pipeline.wakeUp(grab.subLevel);

        player.yBodyRot = player.yHeadRot;
        player.yBodyRotO = player.yHeadRotO;

        boolean isCreativeSuper = player.isCreative() && CommonConfig.COMMON.creativeSuperStrength;

        double strengthMultiplier = 1.0;
        if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
            int amplifier = player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
            if (amplifier == 0) strengthMultiplier = CommonConfig.COMMON.strength1Multiplier;
            else if (amplifier >= 1) strengthMultiplier = CommonConfig.COMMON.strength2Multiplier;
        }
        double actualMaxForce = CommonConfig.COMMON.maxForce * strengthMultiplier;

        Vector3d currentCameraTarget = JOMLConversion.toJOML(player.getEyePosition().add(player.getLookAngle().scale(Math.max(CommonConfig.COMMON.minDistance, grab.distance))));

        boolean isGhostEverything = grab.isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationEverything : CommonConfig.COMMON.ignoreCollisionsGrabEverything;

        boolean wasRotating = grab.isRotating;
        grab.isRotating = grab.rotationTicksLeft > 0;
        grab.rotationTicksLeft = Math.max(0, grab.rotationTicksLeft - 1);

        if (grab.isRotating && !isGhostEverything) {
            grab.subLevel.latestLinearVelocity.set(0, 0, 0);
        }

        if (!grab.isRotating && wasRotating) {
            if (grab.rotateAroundCenter) {
                Vector3d vectorToCOM = new Vector3d(grab.localCenterOfMass).sub(grab.localPivot);
                Vector3d originalCOM = new Vector3d(vectorToCOM).rotate(grab.baseOrientation);
                Vector3d targetCOM = new Vector3d(vectorToCOM).rotate(grab.targetGlobalOrientation);
                grab.accumulatedPivotOffset.add(new Vector3d(originalCOM).sub(targetCOM));
            }

            Vector3d currentActualPivotPos = grab.subLevel.logicalPose().transformPosition(new Vector3d(grab.localPivot));
            grab.anchorGlobalOrigin.set(currentActualPivotPos);
            grab.baseOrientation.set(grab.subLevel.logicalPose().orientation());
            grab.targetGlobalOrientation.set(grab.baseOrientation);
            if (grab.constraintHandle != null) rebuildConstraint(grab);
        }

        Quaterniond relativeRot = new Quaterniond(grab.baseOrientation).invert().mul(grab.targetGlobalOrientation);

        if (grab.constraintHandle != null && relativeRot.angle() > 0.25) {
            if (grab.rotateAroundCenter) {
                Vector3d vectorToCOM = new Vector3d(grab.localCenterOfMass).sub(grab.localPivot);
                Vector3d originalCOM = new Vector3d(vectorToCOM).rotate(grab.baseOrientation);
                Vector3d targetCOM = new Vector3d(vectorToCOM).rotate(grab.targetGlobalOrientation);
                grab.accumulatedPivotOffset.add(new Vector3d(originalCOM).sub(targetCOM));
            }

            grab.baseOrientation.set(grab.targetGlobalOrientation);
            rebuildConstraint(grab);
            relativeRot.identity();
        }

        if (isGhostEverything) {
            Vector3d pivotReference = grab.rotateAroundCenter ? grab.localCenterOfMass : grab.subLevel.logicalPose().rotationPoint();
            Vector3d localOffsetToGrab = new Vector3d(grab.localPivot).sub(pivotReference);
            Vector3d rotatedOffset = new Vector3d(localOffsetToGrab).rotate(grab.targetGlobalOrientation);

            Vector3d targetPos = new Vector3d(currentCameraTarget).add(grab.accumulatedPivotOffset).sub(rotatedOffset);

            grab.pipeline.teleport(grab.subLevel, targetPos, grab.targetGlobalOrientation);
            grab.subLevel.latestLinearVelocity.set(0, 0, 0);
            grab.subLevel.latestAngularVelocity.set(0, 0, 0);
        }

        if (grab.constraintHandle == null) {
            rebuildConstraint(grab);
        }

        Vector3d targetAnchor = new Vector3d(currentCameraTarget).add(grab.accumulatedPivotOffset);

        if (grab.rotateAroundCenter) {
            Vector3d vectorToCOM = new Vector3d(grab.localCenterOfMass).sub(grab.localPivot);
            Vector3d originalCOM = new Vector3d(vectorToCOM).rotate(grab.baseOrientation);
            Vector3d targetCOM = new Vector3d(vectorToCOM).rotate(grab.targetGlobalOrientation);
            targetAnchor.add(new Vector3d(originalCOM).sub(targetCOM));
        }

        Vector3d currentActualGrabBlockPos = grab.subLevel.logicalPose().transformPosition(new Vector3d(grab.localPivot));
        boolean suspendPhysics = false;
        ServerSubLevel standingSubLevel = (ServerSubLevel) Sable.HELPER.getTrackingSubLevel(player);

        if (standingSubLevel != null && standingSubLevel.equals(grab.subLevel)) {
            grab.suspendTicksLeft = CommonConfig.COMMON.standingOnGrabSuspendTicks;
            suspendPhysics = true;
        } else if (grab.suspendTicksLeft > 0) {
            grab.suspendTicksLeft--;
            suspendPhysics = true;
        }

        double eyeDistSq = player.getEyePosition().distanceToSqr(new Vec3(currentActualGrabBlockPos.x, currentActualGrabBlockPos.y, currentActualGrabBlockPos.z));
        double bodyDistSq = player.position().distanceToSqr(currentActualGrabBlockPos.x, currentActualGrabBlockPos.y, currentActualGrabBlockPos.z);

        double eyeSusDistSq = CommonConfig.COMMON.grabProximityEyeSuspendDistance * CommonConfig.COMMON.grabProximityEyeSuspendDistance;
        double bodySusDistSq = CommonConfig.COMMON.grabProximityBodySuspendDistance * CommonConfig.COMMON.grabProximityBodySuspendDistance;

        if (eyeDistSq < eyeSusDistSq || bodyDistSq < bodySusDistSq) {
            suspendPhysics = true;
        }

        double tension = currentActualGrabBlockPos.distance(currentCameraTarget);
        double suspendThresh = isCreativeSuper ? CommonConfig.COMMON.creativeTensionSuspendThreshold : CommonConfig.COMMON.tensionSuspendThreshold;
        double breakThresh = isCreativeSuper ? CommonConfig.COMMON.creativeTensionBreakThreshold : CommonConfig.COMMON.tensionBreakThreshold;

        if (CommonConfig.COMMON.enablePhysicalTether && !player.isCreative() && !player.isSpectator()) {
            double stretch = tension - grab.distance;
            double armStretchTolerance = CommonConfig.COMMON.armStretchTolerance;

            double mass = grab.subLevel.getMassTracker().getMass();
            double objectWeight = mass * CommonConfig.COMMON.physicsGravity;
            double weightRatio = Mth.clamp(objectWeight / actualMaxForce, 0.0, 1.0);
            double encumbrance = Math.min(Math.pow(weightRatio, 2.0), 1.0);

            Vec3 blockPos = new Vec3(currentActualGrabBlockPos.x, currentActualGrabBlockPos.y, currentActualGrabBlockPos.z);
            Vec3 pullDirection = blockPos.subtract(player.getEyePosition()).normalize();
            Vec3 awayDirection = pullDirection.scale(-1.0);
            Vec3 currentVel = player.getDeltaMovement();
            Vec3 newVel = currentVel;

            if (stretch > armStretchTolerance) {
                double activeStretch = stretch - armStretchTolerance;
                double tetherStiffness = CommonConfig.COMMON.tetherStiffnessBase + (CommonConfig.COMMON.tetherStiffnessMultiplier * encumbrance);

                Vec3 correction = pullDirection.scale(activeStretch * tetherStiffness);
                double correctionY = correction.y < 0 ? correction.y : (correction.y * 0.4); // Suavizar tirón vertical para no lanzar al jugador

                newVel = new Vec3(
                        newVel.x + correction.x,
                        newVel.y + correctionY,
                        newVel.z + correction.z
                );
            }

            double awaySpeed = currentVel.dot(awayDirection);
            if (awaySpeed > 0.01 && encumbrance > 0.1) {
                double retrocesoBlock = awaySpeed * encumbrance * CommonConfig.COMMON.pullResistanceMultiplier;
                newVel = newVel.add(awayDirection.scale(-retrocesoBlock));
            }

            double maxAllowedDist = grab.distance + armStretchTolerance + 2.0;
            if (tension > maxAllowedDist) {
                double escapeSpeed = currentVel.dot(awayDirection);
                if (escapeSpeed > 0.01) {
                    newVel = newVel.subtract(awayDirection.scale(escapeSpeed));
                }
            }

            if (newVel.distanceToSqr(currentVel) > 0.0001) {
                player.setDeltaMovement(newVel);
                player.hurtMarked = true;
            }
        }

        if (tension > suspendThresh) {
            if (tension > breakThresh) {
                stopGrabbing(playerId);
                return;
            }
            suspendPhysics = true;
        }

        boolean ignoreEntities = grab.isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationEntities : CommonConfig.COMMON.ignoreCollisionsGrabEntities;
        boolean ignoreOtherPlayers = grab.isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationOtherPlayers : CommonConfig.COMMON.ignoreCollisionsGrabOtherPlayers;
        boolean ignoreSelf = grab.isRotating ? CommonConfig.COMMON.ignoreCollisionsRotationSelf : CommonConfig.COMMON.ignoreCollisionsGrabSelf;

        byte currentMask = 0;
        if (isGhostEverything) currentMask |= 1;
        if (ignoreSelf)        currentMask |= 2;
        if (ignoreOtherPlayers)currentMask |= 4;
        if (ignoreEntities)    currentMask |= 8;

        if (!grab.hasSyncedGhostState || grab.lastCollisionMask != currentMask) {
            grab.lastCollisionMask = currentMask;
            grab.hasSyncedGhostState = true;
            Services.NETWORK.sendGhostStateSync(grab.subLevel, playerId, currentMask);
        }

        Vec3 pVel = player.getDeltaMovement();
        double playerSpeed = pVel.length();

        if (playerSpeed > 0.1) {
            double leadX = pVel.x * 2.0;
            double leadY = pVel.y > 0 ? pVel.y * 2.0 : Math.max(pVel.y * 2.0, -0.5);
            double leadZ = pVel.z * 2.0;

            Vector3d leadOffset = new Vector3d(leadX, leadY, leadZ);
            targetAnchor.add(leadOffset);
        }

        if (grab.constraintHandle != null && !isGhostEverything) {
            Vector3d eulers = new Vector3d();
            relativeRot.getEulerAnglesXYZ(eulers);

            double grabStable = isCreativeSuper ? 1.0 : Math.pow(CommonConfig.COMMON.grabStabilization, 3);
            double rotStable = isCreativeSuper ? 1.0 : Math.pow(CommonConfig.COMMON.rotationStabilization, 3);
            double mass = grab.subLevel.getMassTracker().getMass();

            double horizontalSpeed = Math.sqrt(pVel.x * pVel.x + pVel.z * pVel.z);
            double effectiveSpeed = horizontalSpeed + (pVel.y > 0 ? pVel.y : 0.0);

            double speedMultiplier = 1.0 + (effectiveSpeed * CommonConfig.COMMON.speedStiffnessMultiplierFactor);
            speedMultiplier = Math.min(speedMultiplier, CommonConfig.COMMON.maxSpeedStiffnessMultiplier);

            double baseStiffness   = isCreativeSuper ? CommonConfig.COMMON.stiffness * CommonConfig.COMMON.creativeStrengthMultiplier : CommonConfig.COMMON.stiffness;
            double linearDamping   = isCreativeSuper ? CommonConfig.COMMON.damping * CommonConfig.COMMON.creativeStrengthMultiplier : CommonConfig.COMMON.damping;
            double angularDamping  = isCreativeSuper ? CommonConfig.COMMON.angularDamping * CommonConfig.COMMON.creativeStrengthMultiplier : CommonConfig.COMMON.angularDamping;

            double baseAngularForce   = actualMaxForce * CommonConfig.COMMON.baseAngularForceFactor;
            double stableAngularForce = actualMaxForce * (CommonConfig.COMMON.stableAngularForceMassBase + mass * CommonConfig.COMMON.stableAngularForceMassFactor);

            boolean disableMotors = suspendPhysics;

            double linearMaxForce = disableMotors ? 0.0 : (isCreativeSuper ? 1e12 : actualMaxForce);

            Vector3d globalOffset = new Vector3d(targetAnchor).sub(grab.anchorGlobalOrigin);
            Vector3d localOffset = new Vector3d(globalOffset).rotate(new Quaterniond(grab.baseOrientation).invert());

            double currentLinearStiffness = baseStiffness * speedMultiplier;
            double currentLinearDamping = linearDamping * speedMultiplier;

            grab.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_X, localOffset.x, currentLinearStiffness, currentLinearDamping, true, linearMaxForce);
            grab.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_Y, localOffset.y, currentLinearStiffness, currentLinearDamping, true, linearMaxForce);
            grab.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_Z, localOffset.z, currentLinearStiffness, currentLinearDamping, true, linearMaxForce);

            if (grab.isRotating) {
                double angularStiffness = baseStiffness * (CommonConfig.COMMON.rotatingAngularStiffnessBase + (CommonConfig.COMMON.rotatingAngularStiffnessRange * rotStable));
                double angularMaxForce = disableMotors ? 0.0 : (isCreativeSuper ? 1e12 : (baseAngularForce + ((stableAngularForce - baseAngularForce) * rotStable)));

                grab.constraintHandle.setMotor(ConstraintJointAxis.ANGULAR_X, eulers.x, angularStiffness, angularDamping, true, angularMaxForce);
                grab.constraintHandle.setMotor(ConstraintJointAxis.ANGULAR_Y, eulers.y, angularStiffness, angularDamping, true, angularMaxForce);
                grab.constraintHandle.setMotor(ConstraintJointAxis.ANGULAR_Z, eulers.z, angularStiffness, angularDamping, true, angularMaxForce);

            } else {
                double swayStiffness   = baseStiffness * (CommonConfig.COMMON.swayAngularStiffnessBase + (CommonConfig.COMMON.swayAngularStiffnessRange * grabStable));
                double angularMaxForce = disableMotors ? 0.0 : (isCreativeSuper ? 1e12 : (baseAngularForce + ((stableAngularForce - baseAngularForce) * grabStable)));

                for (ConstraintJointAxis axis : ConstraintJointAxis.ANGULAR) {
                    grab.constraintHandle.setMotor(axis, 0.0, swayStiffness, angularDamping, true, angularMaxForce);
                }
            }

            if (CommonConfig.COMMON.enableEncumbrance && !isCreativeSuper && !player.isSpectator()) {
                double objectWeight = mass * CommonConfig.COMMON.physicsGravity;
                double weightRatio = Mth.clamp(objectWeight / actualMaxForce, 0.0, 1.0);

                double basePenalty = CommonConfig.COMMON.baseMovementPenalty;

                double weightPenalty = weightRatio * CommonConfig.COMMON.weightPenaltyMultiplier;

                double tensionPenalty = 0.0;
                if (tension > grab.distance + 0.5) {
                    double tensionRatio = Mth.clamp((tension - grab.distance) / 5.0, 0.0, 1.0);
                    tensionPenalty = tensionRatio * CommonConfig.COMMON.tensionPenaltyMultiplier;
                }

                Vector3d blockVel = new Vector3d(grab.subLevel.latestLinearVelocity);
                double blockSpeed = blockVel.length();
                double kineticRatio = Mth.clamp(blockSpeed / 1.0, 0.0, 1.0);
                double kineticPenalty = kineticRatio * CommonConfig.COMMON.kineticPenaltyMultiplier;

                double totalPenalty = basePenalty + weightPenalty + tensionPenalty + kineticPenalty;
                totalPenalty = Mth.clamp(totalPenalty, 0.0, CommonConfig.COMMON.maxMovementPenalty);
                totalPenalty = Math.min(totalPenalty, 1.0 - CommonConfig.COMMON.minSpeedWhileGrabbing);

                AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (moveSpeed != null) {
                    moveSpeed.removeModifier(MOVEMENT_PENALTY_ID);

                    if (totalPenalty > 0.01) {
                        AttributeModifier penaltyModifier = new AttributeModifier(
                                MOVEMENT_PENALTY_ID,
                                -totalPenalty,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                        );
                        moveSpeed.addTransientModifier(penaltyModifier);
                    }
                }
            } else {
                AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (moveSpeed != null) {
                    moveSpeed.removeModifier(MOVEMENT_PENALTY_ID);
                }
            }

            if (CommonConfig.COMMON.enableExhaustion && !player.isCreative() && !player.isSpectator() && player.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL) {

                mass = grab.subLevel.getMassTracker().getMass();
                double objectWeight = mass * CommonConfig.COMMON.physicsGravity;

                double freeWeightN = CommonConfig.COMMON.exhaustionPassiveThreshold;
                double effectiveWeight = Math.max(0.0, objectWeight - freeWeightN);
                double weightRatio = Mth.clamp(effectiveWeight / Math.max(1.0, actualMaxForce - freeWeightN), 0.0, 1.0);

                double supportFactor = suspendPhysics ? 0.0 : 1.0;
                Vec3 blockPos = new Vec3(currentActualGrabBlockPos.x, currentActualGrabBlockPos.y, currentActualGrabBlockPos.z);
                double relativeHeight = blockPos.y - player.getY();
                if (relativeHeight < 0.8 && supportFactor > 0) {
                    supportFactor *= 0.5;
                }

                double baseEffort = weightRatio * supportFactor;

                double blockSpeed = grab.subLevel.latestLinearVelocity.length();
                double kineticRatio = Mth.clamp(blockSpeed / 3.0, 0.0, 1.0);
                double kineticEffort = kineticRatio * weightRatio;

                double tensionEffort = 0.0;
                double maxAllowedDist = grab.distance + CommonConfig.COMMON.armStretchTolerance;
                if (tension > maxAllowedDist) {
                    double overStretch = Math.min(tension - maxAllowedDist, 2.0);
                    tensionEffort = (overStretch / 2.0) * weightRatio;
                }

                double exertionRatio = Mth.clamp(baseEffort + kineticEffort + tensionEffort, 0.0, 1.0);

                if (exertionRatio > 0.001) {
                    double dX = player.getX() - player.xo;
                    double dY = player.getY() - player.yo;
                    double dZ = player.getZ() - player.zo;

                    horizontalSpeed = Math.sqrt(dX * dX + dZ * dZ);
                    double verticalSpeed = Math.max(0.0, dY);

                    double weightedSpeed = horizontalSpeed + (verticalSpeed * 4.0);

                    double idleEffort = CommonConfig.COMMON.exhaustionIdleRate * baseEffort;
                    double moveEffort = CommonConfig.COMMON.exhaustionMovementRate * exertionRatio * (weightedSpeed * 20.0);
                    double forceEffort = CommonConfig.COMMON.exhaustionForceRate * (kineticEffort + tensionEffort);

                    float totalExhaustion = (float) (idleEffort + moveEffort + forceEffort);

                    if (totalExhaustion > 0.0f) {
                        player.causeFoodExhaustion(totalExhaustion);
                    }
                }
            }
        }
    }

    public static void onPlayerLoggedOut(Player player) { stopGrabbing(player.getUUID()); }
    public static void onPlayerDeath(Player player) { stopGrabbing(player.getUUID()); }

    public static final double CREATIVE_REACH = 128.0;

    public static double getGrabReach(Player player) {
        double normalReach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue() + CommonConfig.COMMON.grabReachBonus;
        if (player.isCreative() && CommonConfig.COMMON.creativeSuperStrength) {
            return Math.max(CREATIVE_REACH, normalReach);
        }
        return normalReach;
    }
}