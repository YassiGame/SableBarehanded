package dev.juaanp.sablebarehanded.platform;

import dev.juaanp.sablebarehanded.config.NeoForgeGrabConfig;
import dev.juaanp.sablebarehanded.platform.services.IConfigHelper;

public class NeoForgeConfigHelper implements IConfigHelper {
    @Override public double stiffness() { return NeoForgeGrabConfig.stiffness(); }
    @Override public double damping() { return NeoForgeGrabConfig.damping(); }
    @Override public double angularDamping() { return NeoForgeGrabConfig.angularDamping(); }
    @Override public double maxForce() { return NeoForgeGrabConfig.maxForce(); }
    @Override public double minDistance() { return NeoForgeGrabConfig.minDistance(); }

    @Override public boolean enableRotation() { return NeoForgeGrabConfig.enableRotation(); }
    @Override public double grabStabilization() { return NeoForgeGrabConfig.grabStabilization(); }
    @Override public double rotationStabilization() { return NeoForgeGrabConfig.rotationStabilization(); }
    @Override public boolean preventFastRotations() { return NeoForgeGrabConfig.preventFastRotations(); }
    @Override public boolean pivotAtGrabPoint() { return NeoForgeGrabConfig.pivotAtGrabPoint(); }
    @Override public boolean creativeSuperStrength() { return NeoForgeGrabConfig.creativeSuperStrength(); }
    @Override public double strength1Multiplier() { return NeoForgeGrabConfig.strength1Multiplier(); }
    @Override public double strength2Multiplier() { return NeoForgeGrabConfig.strength2Multiplier(); }

    @Override public boolean ignoreCollisionsGrabEverything() { return NeoForgeGrabConfig.ignoreCollisionsGrabEverything(); }
    @Override public boolean ignoreCollisionsGrabEntities() { return NeoForgeGrabConfig.ignoreCollisionsGrabEntities(); }
    @Override public boolean ignoreCollisionsGrabOtherPlayers() { return NeoForgeGrabConfig.ignoreCollisionsGrabOtherPlayers(); }
    @Override public boolean ignoreCollisionsGrabSelf() { return NeoForgeGrabConfig.ignoreCollisionsGrabSelf(); }

    @Override public boolean ignoreCollisionsRotationEverything() { return NeoForgeGrabConfig.ignoreCollisionsRotationEverything(); }
    @Override public boolean ignoreCollisionsRotationEntities() { return NeoForgeGrabConfig.ignoreCollisionsRotationEntities(); }
    @Override public boolean ignoreCollisionsRotationOtherPlayers() { return NeoForgeGrabConfig.ignoreCollisionsRotationOtherPlayers(); }
    @Override public boolean ignoreCollisionsRotationSelf() { return NeoForgeGrabConfig.ignoreCollisionsRotationSelf(); }

    @Override public double rotationMassDampingFactor() { return NeoForgeGrabConfig.rotationMassDampingFactor(); }
    @Override public double tensionSuspendThreshold() { return NeoForgeGrabConfig.tensionSuspendThreshold(); }
    @Override public double tensionBreakThreshold() { return NeoForgeGrabConfig.tensionBreakThreshold(); }
    @Override public double maxPlayerVelocityYUp() { return NeoForgeGrabConfig.maxPlayerVelocityYUp(); }
    @Override public double maxPlayerVelocityYDown() { return NeoForgeGrabConfig.maxPlayerVelocityYDown(); }
    @Override public double maxPlayerVelocityXZ() { return NeoForgeGrabConfig.maxPlayerVelocityXZ(); }
    @Override public double maxRotationSpeed() { return NeoForgeGrabConfig.maxRotationSpeed(); }

    @Override public double rotationSensitivity() { return NeoForgeGrabConfig.rotationSensitivity(); }
    @Override public boolean invertRotation() { return NeoForgeGrabConfig.invertRotation(); }
}