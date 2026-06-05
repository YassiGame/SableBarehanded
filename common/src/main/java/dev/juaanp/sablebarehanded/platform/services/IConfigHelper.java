package dev.juaanp.sablebarehanded.platform.services;

public interface IConfigHelper {
    double stiffness();
    double damping();
    double angularDamping();
    double maxForce();
    double minDistance();

    double rotationMassDampingFactor();
    double tensionSuspendThreshold();
    double tensionBreakThreshold();
    double maxPlayerVelocityYUp();
    double maxPlayerVelocityYDown();
    double maxPlayerVelocityXZ();
    double maxRotationSpeed();

    boolean enableRotation();
    double grabStabilization();
    double rotationStabilization();
    boolean preventFastRotations();
    boolean creativeSuperStrength();

    double strength1Multiplier();
    double strength2Multiplier();

    boolean ignoreCollisionsGrabEverything();
    boolean ignoreCollisionsGrabEntities();
    boolean ignoreCollisionsGrabOtherPlayers();
    boolean ignoreCollisionsGrabSelf();

    boolean ignoreCollisionsRotationEverything();
    boolean ignoreCollisionsRotationEntities();
    boolean ignoreCollisionsRotationOtherPlayers();
    boolean ignoreCollisionsRotationSelf();

    double verticalRotationSensitivity();
    double horizontalRotationSensitivity();
    boolean invertVerticalRotation();
    boolean invertHorizontalRotation();

    boolean rotateAroundCenter();
    void toggleRotateAroundCenter();
    boolean preventMovementWhileRotating();

    boolean enableBarehandedAssembly();
    double barehandedAssemblySpeedMultiplier();
    double barehandedAssemblyMaxDistance();
    boolean preventAssemblyWhenMining();
    double barehandedAssemblyMiningThreshold();
}