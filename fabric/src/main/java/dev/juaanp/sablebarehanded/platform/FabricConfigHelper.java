package dev.juaanp.sablebarehanded.platform;

import dev.juaanp.sablebarehanded.config.FabricGrabConfig;
import dev.juaanp.sablebarehanded.platform.services.IConfigHelper;

public class FabricConfigHelper implements IConfigHelper {
    @Override public double stiffness() { return FabricGrabConfig.COMMON.stiffness; }
    @Override public double damping() { return FabricGrabConfig.COMMON.damping; }
    @Override public double angularDamping() { return FabricGrabConfig.COMMON.angularDamping; }
    @Override public double maxForce() { return FabricGrabConfig.COMMON.maxForce; }
    @Override public double minDistance() { return FabricGrabConfig.COMMON.minDistance; }

    @Override public boolean enableRotation() { return FabricGrabConfig.COMMON.enableRotation; }
    @Override public double grabStabilization() { return FabricGrabConfig.COMMON.grabStabilization; }
    @Override public double rotationStabilization() { return FabricGrabConfig.COMMON.rotationStabilization; }
    @Override public boolean preventFastRotations() { return FabricGrabConfig.COMMON.preventFastRotations; }
    @Override public boolean creativeSuperStrength() { return FabricGrabConfig.COMMON.creativeSuperStrength; }

    @Override public double strength1Multiplier() { return FabricGrabConfig.COMMON.strength1Multiplier; }
    @Override public double strength2Multiplier() { return FabricGrabConfig.COMMON.strength2Multiplier; }

    @Override public boolean ignoreCollisionsGrabEverything() { return FabricGrabConfig.COMMON.ignoreCollisionsGrabEverything; }
    @Override public boolean ignoreCollisionsGrabEntities() { return FabricGrabConfig.COMMON.ignoreCollisionsGrabEntities; }
    @Override public boolean ignoreCollisionsGrabOtherPlayers() { return FabricGrabConfig.COMMON.ignoreCollisionsGrabOtherPlayers; }
    @Override public boolean ignoreCollisionsGrabSelf() { return FabricGrabConfig.COMMON.ignoreCollisionsGrabSelf; }

    @Override public boolean ignoreCollisionsRotationEverything() { return FabricGrabConfig.COMMON.ignoreCollisionsRotationEverything; }
    @Override public boolean ignoreCollisionsRotationEntities() { return FabricGrabConfig.COMMON.ignoreCollisionsRotationEntities; }
    @Override public boolean ignoreCollisionsRotationOtherPlayers() { return FabricGrabConfig.COMMON.ignoreCollisionsRotationOtherPlayers; }
    @Override public boolean ignoreCollisionsRotationSelf() { return FabricGrabConfig.COMMON.ignoreCollisionsRotationSelf; }

    @Override public double rotationMassDampingFactor() { return FabricGrabConfig.COMMON.rotationMassDampingFactor; }
    @Override public double tensionSuspendThreshold() { return FabricGrabConfig.COMMON.tensionSuspendThreshold; }
    @Override public double tensionBreakThreshold() { return FabricGrabConfig.COMMON.tensionBreakThreshold; }
    @Override public double maxPlayerVelocityYUp() { return FabricGrabConfig.COMMON.maxPlayerVelocityYUp; }
    @Override public double maxPlayerVelocityYDown() { return FabricGrabConfig.COMMON.maxPlayerVelocityYDown; }
    @Override public double maxPlayerVelocityXZ() { return FabricGrabConfig.COMMON.maxPlayerVelocityXZ; }
    @Override public double maxRotationSpeed() { return FabricGrabConfig.COMMON.maxRotationSpeed; }

    @Override public double verticalRotationSensitivity() { return FabricGrabConfig.CLIENT.verticalRotationSensitivity; }
    @Override public double horizontalRotationSensitivity() { return FabricGrabConfig.CLIENT.horizontalRotationSensitivity; }
    @Override public boolean invertVerticalRotation() { return FabricGrabConfig.CLIENT.invertVerticalRotation; }
    @Override public boolean invertHorizontalRotation() { return FabricGrabConfig.CLIENT.invertHorizontalRotation; }
    @Override public boolean rotateAroundCenter() { return FabricGrabConfig.CLIENT.rotateAroundCenter; }
    @Override
    public void toggleRotateAroundCenter() {
        FabricGrabConfig.CLIENT.rotateAroundCenter = !FabricGrabConfig.CLIENT.rotateAroundCenter;
        FabricGrabConfig.save();
    }
    @Override public boolean preventMovementWhileRotating() { return FabricGrabConfig.CLIENT.preventMovementWhileRotating; }
}