package dev.juaanp.sablebarehanded.physics;

import dev.juaanp.sablebarehanded.api.SableBarehandedEvents;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrabPhysicsManager {

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

                boolean ignoreEverything = isRotating ? Services.CONFIG.ignoreCollisionsRotationEverything() : Services.CONFIG.ignoreCollisionsGrabEverything();
                if (ignoreEverything) return true;

                if (entity instanceof Player player) {
                    boolean ignoreSelf = isRotating ? Services.CONFIG.ignoreCollisionsRotationSelf() : Services.CONFIG.ignoreCollisionsGrabSelf();
                    boolean ignoreOthers = isRotating ? Services.CONFIG.ignoreCollisionsRotationOtherPlayers() : Services.CONFIG.ignoreCollisionsGrabOtherPlayers();
                    return player.getUUID().equals(grabberId) ? ignoreSelf : ignoreOthers;
                } else {
                    return isRotating ? Services.CONFIG.ignoreCollisionsRotationEntities() : Services.CONFIG.ignoreCollisionsGrabEntities();
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

    private static void performGrab(Player player, ServerSubLevel serverSubLevel, org.joml.Vector3d localGrabBlock) {        Level level = player.level();
        ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(level);
        if (container == null) return;

        PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

        Vector3d localCenterOfMass = new Vector3d(serverSubLevel.getMassTracker().getCenterOfMass());

        Vector3d globalGrabBlockPos = serverSubLevel.logicalPose().transformPosition(new Vector3d(localGrabBlock));
        float distance = (float) player.getEyePosition().distanceTo(new Vec3(globalGrabBlockPos.x, globalGrabBlockPos.y, globalGrabBlockPos.z));

        Vector3d crosshairTarget = JOMLConversion.toJOML(player.getEyePosition().add(player.getLookAngle().scale(Math.max(Services.CONFIG.minDistance(), distance))));
        Quaterniond initialOrient = serverSubLevel.logicalPose().orientation();

        GrabSession session = new GrabSession(serverSubLevel, distance, localGrabBlock, localCenterOfMass, crosshairTarget, initialOrient, pipeline);

        pipeline.wakeUp(serverSubLevel);

        rebuildConstraint(session);
        Services.NETWORK.sendStartGrabbingAnimation(player);
        ACTIVE_GRABS.put(player.getUUID(), session);
    }

    public static void startGrabbing(Player player, BlockPos pos) {
        Level level = player.level();
        if (level.isClientSide() || ACTIVE_GRABS.containsKey(player.getUUID()) || !player.getMainHandItem().isEmpty()) return;

        double reach = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE).getValue() + 2.0;
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

        double reach = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE).getValue() + 2.0;
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > (reach * reach)) return;

        double maxDist = Services.CONFIG.barehandedAssemblyMaxDistance() + 1.0;
        if (player.getEyePosition().distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos)) > (maxDist * maxDist)) return;

        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        if (dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper.isIgnored(level, pos, state)) return;

        java.util.List<BlockPos> blocks = new java.util.ArrayList<>();
        blocks.add(pos);

        if (state.getBlock() instanceof net.minecraft.world.level.block.ChestBlock) {
            net.minecraft.world.level.block.state.properties.ChestType type = state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE);
            if (type != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
                blocks.add(pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state)));
            }
        }
        else if (dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper.isLiftableDecor(level.getBlockState(pos.above()))) {
            blocks.add(pos.above());
        }
        else if (dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper.isLiftableDecor(state)) {
            blocks.add(pos.below());
        }

        if (dev.ryanhcode.sable.Sable.HELPER.getContaining(level, pos) != null) return;

        dev.ryanhcode.sable.companion.math.BoundingBox3i bounds = dev.ryanhcode.sable.companion.math.BoundingBox3i.from(blocks);
        dev.ryanhcode.sable.sublevel.SubLevel subLevel = dev.ryanhcode.sable.api.SubLevelAssemblyHelper.assembleBlocks((net.minecraft.server.level.ServerLevel) level, pos, blocks, bounds);

        if (subLevel instanceof dev.ryanhcode.sable.sublevel.ServerSubLevel serverSubLevel) {

            boolean isFastLift = dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper.isFastLift(level, pos, state);
            if (!isFastLift) {
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.5f);
            } else {
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_PICKUP, net.minecraft.sounds.SoundSource.PLAYERS, 0.2f, 0.5f);
            }

            dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer container = (dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer) dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(level);
            if (container != null) {
                container.physicsSystem().getPipeline().wakeUp(serverSubLevel);
            }

            org.joml.Vector3d globalVec = new org.joml.Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            org.joml.Vector3d localGrabBlock = serverSubLevel.logicalPose().transformPositionInverse(globalVec, new org.joml.Vector3d());

            performGrab(player, serverSubLevel, localGrabBlock);
            dev.juaanp.sablebarehanded.api.SableBarehandedEvents.fireOnGrab(player, serverSubLevel);
        }
    }

    public static void applyRotation(Player player, double yaw, double pitch, boolean clientPrefersCenter) {
        if (!Services.CONFIG.enableRotation()) return;

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

        boolean isCreativeSuper = player.isCreative() && Services.CONFIG.creativeSuperStrength();
        double mass       = grab.subLevel.getMassTracker().getMass();
        double massFactor = isCreativeSuper ? 1.0 : (1.0 / (1.0 + mass * Services.CONFIG.rotationMassDampingFactor()));

        double yawDelta   = yaw   * massFactor;
        double pitchDelta = pitch * massFactor;

        if (Services.CONFIG.preventFastRotations()) {
            yawDelta   = Mth.clamp(yawDelta,   -Services.CONFIG.maxRotationSpeed(), Services.CONFIG.maxRotationSpeed());
            pitchDelta = Mth.clamp(pitchDelta, -Services.CONFIG.maxRotationSpeed(), Services.CONFIG.maxRotationSpeed());
        }

        final Vec3 look    = player.getLookAngle();
        final Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);

        Vec3   right = look.cross(worldUp);
        double rLen  = right.length();
        if (rLen < 1e-6) {
            double yr = Math.toRadians(player.getYRot());
            right = new Vec3(Math.cos(yr), 0.0, Math.sin(yr));
        } else {
            right = right.scale(1.0 / rLen);
        }

        final Vec3 camUp = right.cross(look).normalize();

        final double rx    = right.x * pitchDelta + camUp.x * yawDelta;
        final double ry    = right.y * pitchDelta + camUp.y * yawDelta;
        final double rz    = right.z * pitchDelta + camUp.z * yawDelta;
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
        if (Math.abs(vel.x) > Services.CONFIG.maxPlayerVelocityXZ() || vel.y > Services.CONFIG.maxPlayerVelocityYUp() || vel.y < Services.CONFIG.maxPlayerVelocityYDown() || Math.abs(vel.z) > Services.CONFIG.maxPlayerVelocityXZ()) {
            player.setDeltaMovement(
                    Mth.clamp(vel.x, -Services.CONFIG.maxPlayerVelocityXZ(), Services.CONFIG.maxPlayerVelocityXZ()),
                    Mth.clamp(vel.y, Services.CONFIG.maxPlayerVelocityYDown(), Services.CONFIG.maxPlayerVelocityYUp()),
                    Mth.clamp(vel.z, -Services.CONFIG.maxPlayerVelocityXZ(), Services.CONFIG.maxPlayerVelocityXZ())
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

        boolean isCreativeSuper = player.isCreative() && Services.CONFIG.creativeSuperStrength();

        double strengthMultiplier = 1.0;
        if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
            int amplifier = player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
            if (amplifier == 0) strengthMultiplier = Services.CONFIG.strength1Multiplier();
            else if (amplifier >= 1) strengthMultiplier = Services.CONFIG.strength2Multiplier();
        }
        double actualMaxForce = Services.CONFIG.maxForce() * strengthMultiplier;

        Vector3d currentCameraTarget = JOMLConversion.toJOML(player.getEyePosition().add(player.getLookAngle().scale(Math.max(Services.CONFIG.minDistance(), grab.distance))));

        boolean isGhostEverything = grab.isRotating ? Services.CONFIG.ignoreCollisionsRotationEverything() : Services.CONFIG.ignoreCollisionsGrabEverything();

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
            grab.suspendTicksLeft = 15;
            suspendPhysics = true;
        } else if (grab.suspendTicksLeft > 0) {
            grab.suspendTicksLeft--;
            suspendPhysics = true;
        }

        if (player.getEyePosition().distanceToSqr(new Vec3(currentActualGrabBlockPos.x, currentActualGrabBlockPos.y, currentActualGrabBlockPos.z)) < 1.0 ||
                player.position().distanceToSqr(currentActualGrabBlockPos.x, currentActualGrabBlockPos.y, currentActualGrabBlockPos.z) < 2.0) {
            suspendPhysics = true;
        }

        double tension = currentActualGrabBlockPos.distance(currentCameraTarget);
        double suspendThresh = isCreativeSuper ? 64.0 : Services.CONFIG.tensionSuspendThreshold();
        double breakThresh = isCreativeSuper ? 64.0 : Services.CONFIG.tensionBreakThreshold();

        if (tension > suspendThresh) {
            if (tension > breakThresh) {
                stopGrabbing(playerId);
                return;
            }
            suspendPhysics = true;
        }

        boolean ignoreEntities = grab.isRotating ? Services.CONFIG.ignoreCollisionsRotationEntities() : Services.CONFIG.ignoreCollisionsGrabEntities();
        boolean ignoreOtherPlayers = grab.isRotating ? Services.CONFIG.ignoreCollisionsRotationOtherPlayers() : Services.CONFIG.ignoreCollisionsGrabOtherPlayers();
        boolean ignoreSelf = grab.isRotating ? Services.CONFIG.ignoreCollisionsRotationSelf() : Services.CONFIG.ignoreCollisionsGrabSelf();

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

            double grabStable = isCreativeSuper ? 1.0 : Math.pow(Services.CONFIG.grabStabilization(), 3);
            double rotStable  = isCreativeSuper ? 1.0 : Math.pow(Services.CONFIG.rotationStabilization(), 3);
            double mass       = grab.subLevel.getMassTracker().getMass();

            double horizontalSpeed = Math.sqrt(pVel.x * pVel.x + pVel.z * pVel.z);
            double effectiveSpeed = horizontalSpeed + (pVel.y > 0 ? pVel.y : 0.0);

            double speedMultiplier = 1.0 + (effectiveSpeed * 15.0);
            speedMultiplier = Math.min(speedMultiplier, 8.0);

            double baseStiffness   = isCreativeSuper ? Services.CONFIG.stiffness() * 10.0 : Services.CONFIG.stiffness();
            double linearDamping   = isCreativeSuper ? Services.CONFIG.damping() * 10.0 : Services.CONFIG.damping();
            double angularDamping  = isCreativeSuper ? Services.CONFIG.angularDamping() * 10.0 : Services.CONFIG.angularDamping();

            double baseAngularForce   = actualMaxForce * 0.15;
            double stableAngularForce = actualMaxForce * (10.0 + mass * 0.5);

            boolean disableMotors  = suspendPhysics;

            double linearMaxForce  = disableMotors ? 0.0 : (isCreativeSuper ? 1e12 : (actualMaxForce * speedMultiplier));

            Vector3d globalOffset = new Vector3d(targetAnchor).sub(grab.anchorGlobalOrigin);
            Vector3d localOffset  = new Vector3d(globalOffset).rotate(new Quaterniond(grab.baseOrientation).invert());

            double currentLinearStiffness = baseStiffness * speedMultiplier;
            double currentLinearDamping   = linearDamping * speedMultiplier;

            grab.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_X, localOffset.x, currentLinearStiffness, currentLinearDamping, true, linearMaxForce);
            grab.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_Y, localOffset.y, currentLinearStiffness, currentLinearDamping, true, linearMaxForce);
            grab.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_Z, localOffset.z, currentLinearStiffness, currentLinearDamping, true, linearMaxForce);

            if (grab.isRotating) {
                double angularStiffness = baseStiffness * (1.5 + (4.5 * rotStable));
                double angularMaxForce  = disableMotors ? 0.0 : (isCreativeSuper ? 1e12 : (baseAngularForce + ((stableAngularForce - baseAngularForce) * rotStable)));

                grab.constraintHandle.setMotor(ConstraintJointAxis.ANGULAR_X, eulers.x, angularStiffness, angularDamping, true, angularMaxForce);
                grab.constraintHandle.setMotor(ConstraintJointAxis.ANGULAR_Y, eulers.y, angularStiffness, angularDamping, true, angularMaxForce);
                grab.constraintHandle.setMotor(ConstraintJointAxis.ANGULAR_Z, eulers.z, angularStiffness, angularDamping, true, angularMaxForce);

            } else {
                double swayStiffness   = baseStiffness * (0.6 + (5.4 * grabStable));
                double angularMaxForce = disableMotors ? 0.0 : (isCreativeSuper ? 1e12 : (baseAngularForce + ((stableAngularForce - baseAngularForce) * grabStable)));

                for (ConstraintJointAxis axis : ConstraintJointAxis.ANGULAR) {
                    grab.constraintHandle.setMotor(axis, 0.0, swayStiffness, angularDamping, true, angularMaxForce);
                }
            }
        }
    }

    public static void onPlayerLoggedOut(Player player) { stopGrabbing(player.getUUID()); }
    public static void onPlayerDeath(Player player)     { stopGrabbing(player.getUUID()); }
}