package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.CommonConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BarehandedConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Sable Barehanded"));
        builder.setSavingRunnable(CommonConfig::save);

        ConfigCategory physics = builder.getOrCreateCategory(Component.literal("Server Settings"));
        ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client Settings"));
        ConfigEntryBuilder eb = builder.entryBuilder();

        // Server Settings Subcategories
        SubCategoryBuilder core = eb.startSubCategory(Component.literal("Core Grab Mechanics"));
        addDouble(core, eb, Component.literal("Max Force (N)"), CommonConfig.Specs.MAX_FORCE, CommonConfig.COMMON.maxForce, v -> CommonConfig.COMMON.maxForce = v);
        addDouble(core, eb, Component.literal("Min Distance (m)"), CommonConfig.Specs.MIN_DISTANCE, CommonConfig.COMMON.minDistance, v -> CommonConfig.COMMON.minDistance = v);
        addDouble(core, eb, Component.literal("Grab Reach Bonus (m)"), CommonConfig.Specs.GRAB_REACH_BONUS, CommonConfig.COMMON.grabReachBonus, v -> CommonConfig.COMMON.grabReachBonus = v);
        addDouble(core, eb, Component.literal("Grab Stabilization"), CommonConfig.Specs.GRAB_STABILIZATION, CommonConfig.COMMON.grabStabilization, v -> CommonConfig.COMMON.grabStabilization = v);
        addBoolean(core, eb, Component.literal("Creative Super Strength"), CommonConfig.Specs.CREATIVE_SUPER_STRENGTH, CommonConfig.COMMON.creativeSuperStrength, v -> CommonConfig.COMMON.creativeSuperStrength = v);
        addDouble(core, eb, Component.literal("Strength I Multiplier"), CommonConfig.Specs.STRENGTH_1_MULTIPLIER, CommonConfig.COMMON.strength1Multiplier, v -> CommonConfig.COMMON.strength1Multiplier = v);
        addDouble(core, eb, Component.literal("Strength II Multiplier"), CommonConfig.Specs.STRENGTH_2_MULTIPLIER, CommonConfig.COMMON.strength2Multiplier, v -> CommonConfig.COMMON.strength2Multiplier = v);
        physics.addEntry(core.build());

        SubCategoryBuilder rotation = eb.startSubCategory(Component.literal("Rotation Mechanics"));
        addBoolean(rotation, eb, Component.literal("Enable Rotation"), CommonConfig.Specs.ENABLE_ROTATION, CommonConfig.COMMON.enableRotation, v -> CommonConfig.COMMON.enableRotation = v);
        addDouble(rotation, eb, Component.literal("Rotation Stabilization"), CommonConfig.Specs.ROTATION_STABILIZATION, CommonConfig.COMMON.rotationStabilization, v -> CommonConfig.COMMON.rotationStabilization = v);
        addDouble(rotation, eb, Component.literal("Max Rotation Speed"), CommonConfig.Specs.MAX_ROTATION_SPEED, CommonConfig.COMMON.maxRotationSpeed, v -> CommonConfig.COMMON.maxRotationSpeed = v);
        addBoolean(rotation, eb, Component.literal("Prevent Fast Rotations"), CommonConfig.Specs.PREVENT_FAST_ROTATIONS, CommonConfig.COMMON.preventFastRotations, v -> CommonConfig.COMMON.preventFastRotations = v);
        addDouble(rotation, eb, Component.literal("Rotation Mass Damping Factor"), CommonConfig.Specs.ROTATION_MASS_DAMPING_FACTOR, CommonConfig.COMMON.rotationMassDampingFactor, v -> CommonConfig.COMMON.rotationMassDampingFactor = v);
        addInt(rotation, eb, Component.literal("Rotation Ticks Window"), CommonConfig.Specs.ROTATION_TICKS_WINDOW, CommonConfig.COMMON.rotationTicksWindow, v -> CommonConfig.COMMON.rotationTicksWindow = v);
        addDouble(rotation, eb, Component.literal("Rotation Rebuild Threshold (rad)"), CommonConfig.Specs.ROTATION_REBUILD_THRESHOLD, CommonConfig.COMMON.rotationRebuildThreshold, v -> CommonConfig.COMMON.rotationRebuildThreshold = v);
        physics.addEntry(rotation.build());

        SubCategoryBuilder assembly = eb.startSubCategory(Component.literal("Block Detach & Pull Mechanics"));
        addBoolean(assembly, eb, Component.literal("Enable Assembly"), CommonConfig.Specs.ENABLE_BAREHANDED_ASSEMBLY, CommonConfig.COMMON.enableBarehandedAssembly, v -> CommonConfig.COMMON.enableBarehandedAssembly = v);
        addDouble(assembly, eb, Component.literal("Detach Speed Multiplier"), CommonConfig.Specs.BAREHANDED_ASSEMBLY_SPEED_MULTIPLIER, CommonConfig.COMMON.barehandedAssemblySpeedMultiplier, v -> CommonConfig.COMMON.barehandedAssemblySpeedMultiplier = v);
        addDouble(assembly, eb, Component.literal("Detach Max Distance (m)"), CommonConfig.Specs.BAREHANDED_ASSEMBLY_MAX_DISTANCE, CommonConfig.COMMON.barehandedAssemblyMaxDistance, v -> CommonConfig.COMMON.barehandedAssemblyMaxDistance = v);
        addDouble(assembly, eb, Component.literal("Server Distance Tolerance (m)"), CommonConfig.Specs.ASSEMBLY_SERVER_DISTANCE_TOLERANCE, CommonConfig.COMMON.assemblyServerDistanceTolerance, v -> CommonConfig.COMMON.assemblyServerDistanceTolerance = v);
        addDouble(assembly, eb, Component.literal("Client Distance Tolerance (m)"), CommonConfig.Specs.ASSEMBLY_CLIENT_DISTANCE_TOLERANCE, CommonConfig.COMMON.assemblyClientDistanceTolerance, v -> CommonConfig.COMMON.assemblyClientDistanceTolerance = v);
        addInt(assembly, eb, Component.literal("Fast-Lift Assembly Ticks"), CommonConfig.Specs.FAST_LIFT_ASSEMBLY_TICKS, CommonConfig.COMMON.fastLiftAssemblyTicks, v -> CommonConfig.COMMON.fastLiftAssemblyTicks = v);
        addDouble(assembly, eb, Component.literal("Pull Threshold (m)"), CommonConfig.Specs.PULL_THRESHOLD, CommonConfig.COMMON.pullThreshold, v -> CommonConfig.COMMON.pullThreshold = v);
        addDouble(assembly, eb, Component.literal("Pull Resistance Multiplier"), CommonConfig.Specs.PULL_RESISTANCE_MULTIPLIER, CommonConfig.COMMON.pullResistanceMultiplier, v -> CommonConfig.COMMON.pullResistanceMultiplier = v);
        addDouble(assembly, eb, Component.literal("Movement Damping"), CommonConfig.Specs.ASSEMBLY_MOVEMENT_DAMPING, CommonConfig.COMMON.assemblyMovementDamping, v -> CommonConfig.COMMON.assemblyMovementDamping = v);
        physics.addEntry(assembly.build());

        SubCategoryBuilder encumbrance = eb.startSubCategory(Component.literal("Encumbrance & Tethering "));
        addBoolean(encumbrance, eb, Component.literal("Enable Encumbrance "), CommonConfig.Specs.ENABLE_ENCUMBRANCE, CommonConfig.COMMON.enableEncumbrance, v -> CommonConfig.COMMON.enableEncumbrance = v);
        addDouble(encumbrance, eb, Component.literal("Physics Gravity (m/s^2) "), CommonConfig.Specs.PHYSICS_GRAVITY, CommonConfig.COMMON.physicsGravity, v -> CommonConfig.COMMON.physicsGravity = v);
        addDouble(encumbrance, eb, Component.literal("Max Movement Penalty "), CommonConfig.Specs.MAX_MOVEMENT_PENALTY, CommonConfig.COMMON.maxMovementPenalty, v -> CommonConfig.COMMON.maxMovementPenalty = v);
        addDouble(encumbrance, eb, Component.literal("Jump Prevention Threshold "), CommonConfig.Specs.JUMP_PREVENTION_THRESHOLD, CommonConfig.COMMON.jumpPreventionThreshold, v -> CommonConfig.COMMON.jumpPreventionThreshold = v);
        addDouble(encumbrance, eb, Component.literal("Max Camera Penalty "), CommonConfig.Specs.MAX_CAMERA_PENALTY, CommonConfig.COMMON.maxCameraPenalty, v -> CommonConfig.COMMON.maxCameraPenalty = v);
        addBoolean(encumbrance, eb, Component.literal("Enable Physical Tether"), CommonConfig.Specs.ENABLE_PHYSICAL_TETHER, CommonConfig.COMMON.enablePhysicalTether, v -> CommonConfig.COMMON.enablePhysicalTether = v);
        addDouble(encumbrance, eb, Component.literal("Arm Stretch Tolerance (m)"), CommonConfig.Specs.ARM_STRETCH_TOLERANCE, CommonConfig.COMMON.armStretchTolerance, v -> CommonConfig.COMMON.armStretchTolerance = v);
        addDouble(encumbrance, eb, Component.literal("Tether Stiffness Base"), CommonConfig.Specs.TETHER_STIFFNESS_BASE, CommonConfig.COMMON.tetherStiffnessBase, v -> CommonConfig.COMMON.tetherStiffnessBase = v);
        addDouble(encumbrance, eb, Component.literal("Tether Stiffness Multiplier"), CommonConfig.Specs.TETHER_STIFFNESS_MULTIPLIER, CommonConfig.COMMON.tetherStiffnessMultiplier, v -> CommonConfig.COMMON.tetherStiffnessMultiplier = v);
        physics.addEntry(encumbrance.build());

        SubCategoryBuilder exhaustion = eb.startSubCategory(Component.literal("Hunger & Exhaustion"));
        addBoolean(exhaustion, eb, Component.literal("Enable Exhaustion"), CommonConfig.Specs.ENABLE_EXHAUSTION, CommonConfig.COMMON.enableExhaustion, v -> CommonConfig.COMMON.enableExhaustion = v);
        addDouble(exhaustion, eb, Component.literal("Idle Drain Rate"), CommonConfig.Specs.EXHAUSTION_IDLE_RATE, CommonConfig.COMMON.exhaustionIdleRate, v -> CommonConfig.COMMON.exhaustionIdleRate = v);
        addDouble(exhaustion, eb, Component.literal("Movement Drain Rate"), CommonConfig.Specs.EXHAUSTION_MOVEMENT_RATE, CommonConfig.COMMON.exhaustionMovementRate, v -> CommonConfig.COMMON.exhaustionMovementRate = v);
        addDouble(exhaustion, eb, Component.literal("Tension Drain Rate (Pulling)"), CommonConfig.Specs.EXHAUSTION_TENSION_RATE, CommonConfig.COMMON.exhaustionTensionRate, v -> CommonConfig.COMMON.exhaustionTensionRate = v);
        addDouble(exhaustion, eb, Component.literal("Force Drain Rate (Kinetic)"), CommonConfig.Specs.EXHAUSTION_FORCE_RATE, CommonConfig.COMMON.exhaustionForceRate, v -> CommonConfig.COMMON.exhaustionForceRate = v);
        addDouble(exhaustion, eb, Component.literal("Passive Force Threshold"), CommonConfig.Specs.EXHAUSTION_PASSIVE_THRESHOLD, CommonConfig.COMMON.exhaustionPassiveThreshold, v -> CommonConfig.COMMON.exhaustionPassiveThreshold = v);
        physics.addEntry(exhaustion.build());

        SubCategoryBuilder suspension = eb.startSubCategory(Component.literal("Physics Suspension"));
        addInt(suspension, eb, Component.literal("Standing-on-Grab Suspend Ticks"), CommonConfig.Specs.STANDING_ON_GRAB_SUSPEND_TICKS, CommonConfig.COMMON.standingOnGrabSuspendTicks, v -> CommonConfig.COMMON.standingOnGrabSuspendTicks = v);
        addDouble(suspension, eb, Component.literal("Proximity Eye Suspend Distance (m)"), CommonConfig.Specs.GRAB_PROXIMITY_EYE_SUSPEND_DISTANCE, CommonConfig.COMMON.grabProximityEyeSuspendDistance, v -> CommonConfig.COMMON.grabProximityEyeSuspendDistance = v);
        addDouble(suspension, eb, Component.literal("Proximity Body Suspend Distance (m)"), CommonConfig.Specs.GRAB_PROXIMITY_BODY_SUSPEND_DISTANCE, CommonConfig.COMMON.grabProximityBodySuspendDistance, v -> CommonConfig.COMMON.grabProximityBodySuspendDistance = v);
        addDouble(suspension, eb, Component.literal("Tension Suspend Threshold"), CommonConfig.Specs.TENSION_SUSPEND_THRESHOLD, CommonConfig.COMMON.tensionSuspendThreshold, v -> CommonConfig.COMMON.tensionSuspendThreshold = v);
        addDouble(suspension, eb, Component.literal("Tension Break Threshold"), CommonConfig.Specs.TENSION_BREAK_THRESHOLD, CommonConfig.COMMON.tensionBreakThreshold, v -> CommonConfig.COMMON.tensionBreakThreshold = v);
        addDouble(suspension, eb, Component.literal("Creative Tension Suspend Threshold"), CommonConfig.Specs.CREATIVE_TENSION_SUSPEND_THRESHOLD, CommonConfig.COMMON.creativeTensionSuspendThreshold, v -> CommonConfig.COMMON.creativeTensionSuspendThreshold = v);
        addDouble(suspension, eb, Component.literal("Creative Tension Break Threshold"), CommonConfig.Specs.CREATIVE_TENSION_BREAK_THRESHOLD, CommonConfig.COMMON.creativeTensionBreakThreshold, v -> CommonConfig.COMMON.creativeTensionBreakThreshold = v);
        physics.addEntry(suspension.build());

        SubCategoryBuilder velocity = eb.startSubCategory(Component.literal("Player Velocity Limits"));
        addDouble(velocity, eb, Component.literal("Max Velocity Y Up (m/t)"), CommonConfig.Specs.MAX_PLAYER_VELOCITY_Y_UP, CommonConfig.COMMON.maxPlayerVelocityYUp, v -> CommonConfig.COMMON.maxPlayerVelocityYUp = v);
        addDouble(velocity, eb, Component.literal("Max Velocity Y Down (m/t)"), CommonConfig.Specs.MAX_PLAYER_VELOCITY_Y_DOWN, CommonConfig.COMMON.maxPlayerVelocityYDown, v -> CommonConfig.COMMON.maxPlayerVelocityYDown = v);
        addDouble(velocity, eb, Component.literal("Max Velocity XZ (m/t)"), CommonConfig.Specs.MAX_PLAYER_VELOCITY_XZ, CommonConfig.COMMON.maxPlayerVelocityXZ, v -> CommonConfig.COMMON.maxPlayerVelocityXZ = v);
        physics.addEntry(velocity.build());

        SubCategoryBuilder colls = eb.startSubCategory(Component.literal("Collision Filters"));
        addBoolean(colls, eb, Component.literal("[Grab] Ignore Self"), CommonConfig.Specs.IGNORE_COLLISIONS_GRAB_SELF, CommonConfig.COMMON.ignoreCollisionsGrabSelf, v -> CommonConfig.COMMON.ignoreCollisionsGrabSelf = v);
        addBoolean(colls, eb, Component.literal("[Grab] Ignore Other Players"), CommonConfig.Specs.IGNORE_COLLISIONS_GRAB_OTHER_PLAYERS, CommonConfig.COMMON.ignoreCollisionsGrabOtherPlayers, v -> CommonConfig.COMMON.ignoreCollisionsGrabOtherPlayers = v);
        addBoolean(colls, eb, Component.literal("[Grab] Ignore Entities"), CommonConfig.Specs.IGNORE_COLLISIONS_GRAB_ENTITIES, CommonConfig.COMMON.ignoreCollisionsGrabEntities, v -> CommonConfig.COMMON.ignoreCollisionsGrabEntities = v);
        addBoolean(colls, eb, Component.literal("[Grab] Ignore Everything"), CommonConfig.Specs.IGNORE_COLLISIONS_GRAB_EVERYTHING, CommonConfig.COMMON.ignoreCollisionsGrabEverything, v -> CommonConfig.COMMON.ignoreCollisionsGrabEverything = v);
        addBoolean(colls, eb, Component.literal("[Rotate] Ignore Self"), CommonConfig.Specs.IGNORE_COLLISIONS_ROTATION_SELF, CommonConfig.COMMON.ignoreCollisionsRotationSelf, v -> CommonConfig.COMMON.ignoreCollisionsRotationSelf = v);
        addBoolean(colls, eb, Component.literal("[Rotate] Ignore Other Players"), CommonConfig.Specs.IGNORE_COLLISIONS_ROTATION_OTHER_PLAYERS, CommonConfig.COMMON.ignoreCollisionsRotationOtherPlayers, v -> CommonConfig.COMMON.ignoreCollisionsRotationOtherPlayers = v);
        addBoolean(colls, eb, Component.literal("[Rotate] Ignore Entities"), CommonConfig.Specs.IGNORE_COLLISIONS_ROTATION_ENTITIES, CommonConfig.COMMON.ignoreCollisionsRotationEntities, v -> CommonConfig.COMMON.ignoreCollisionsRotationEntities = v);
        addBoolean(colls, eb, Component.literal("[Rotate] Ignore Everything"), CommonConfig.Specs.IGNORE_COLLISIONS_ROTATION_EVERYTHING, CommonConfig.COMMON.ignoreCollisionsRotationEverything, v -> CommonConfig.COMMON.ignoreCollisionsRotationEverything = v);
        physics.addEntry(colls.build());

        SubCategoryBuilder advanced = eb.startSubCategory(Component.literal("Advanced Physics Tuning"));
        addDouble(advanced, eb, Component.literal("Stiffness"), CommonConfig.Specs.STIFFNESS, CommonConfig.COMMON.stiffness, v -> CommonConfig.COMMON.stiffness = v);
        addDouble(advanced, eb, Component.literal("Damping"), CommonConfig.Specs.DAMPING, CommonConfig.COMMON.damping, v -> CommonConfig.COMMON.damping = v);
        addDouble(advanced, eb, Component.literal("Angular Damping"), CommonConfig.Specs.ANGULAR_DAMPING, CommonConfig.COMMON.angularDamping, v -> CommonConfig.COMMON.angularDamping = v);
        addDouble(advanced, eb, Component.literal("Creative Strength Multiplier"), CommonConfig.Specs.CREATIVE_STRENGTH_MULTIPLIER, CommonConfig.COMMON.creativeStrengthMultiplier, v -> CommonConfig.COMMON.creativeStrengthMultiplier = v);
        addDouble(advanced, eb, Component.literal("Speed Stiffness Factor"), CommonConfig.Specs.SPEED_STIFFNESS_MULTIPLIER_FACTOR, CommonConfig.COMMON.speedStiffnessMultiplierFactor, v -> CommonConfig.COMMON.speedStiffnessMultiplierFactor = v);
        addDouble(advanced, eb, Component.literal("Max Speed Stiffness Mult"), CommonConfig.Specs.MAX_SPEED_STIFFNESS_MULTIPLIER, CommonConfig.COMMON.maxSpeedStiffnessMultiplier, v -> CommonConfig.COMMON.maxSpeedStiffnessMultiplier = v);
        addDouble(advanced, eb, Component.literal("Base Angular Force Factor"), CommonConfig.Specs.BASE_ANGULAR_FORCE_FACTOR, CommonConfig.COMMON.baseAngularForceFactor, v -> CommonConfig.COMMON.baseAngularForceFactor = v);
        addDouble(advanced, eb, Component.literal("Stable Angular Force: Mass Base"), CommonConfig.Specs.STABLE_ANGULAR_FORCE_MASS_BASE, CommonConfig.COMMON.stableAngularForceMassBase, v -> CommonConfig.COMMON.stableAngularForceMassBase = v);
        addDouble(advanced, eb, Component.literal("Stable Angular Force: Mass Factor"), CommonConfig.Specs.STABLE_ANGULAR_FORCE_MASS_FACTOR, CommonConfig.COMMON.stableAngularForceMassFactor, v -> CommonConfig.COMMON.stableAngularForceMassFactor = v);
        addDouble(advanced, eb, Component.literal("Rotating Angular Stiffness: Base"), CommonConfig.Specs.ROTATING_ANGULAR_STIFFNESS_BASE, CommonConfig.COMMON.rotatingAngularStiffnessBase, v -> CommonConfig.COMMON.rotatingAngularStiffnessBase = v);
        addDouble(advanced, eb, Component.literal("Rotating Angular Stiffness: Range"), CommonConfig.Specs.ROTATING_ANGULAR_STIFFNESS_RANGE, CommonConfig.COMMON.rotatingAngularStiffnessRange, v -> CommonConfig.COMMON.rotatingAngularStiffnessRange = v);
        addDouble(advanced, eb, Component.literal("Sway Angular Stiffness: Base"), CommonConfig.Specs.SWAY_ANGULAR_STIFFNESS_BASE, CommonConfig.COMMON.swayAngularStiffnessBase, v -> CommonConfig.COMMON.swayAngularStiffnessBase = v);
        addDouble(advanced, eb, Component.literal("Sway Angular Stiffness: Range"), CommonConfig.Specs.SWAY_ANGULAR_STIFFNESS_RANGE, CommonConfig.COMMON.swayAngularStiffnessRange, v -> CommonConfig.COMMON.swayAngularStiffnessRange = v);
        physics.addEntry(advanced.build());

        SubCategoryBuilder movementPenalty = eb.startSubCategory(Component.literal("Movement Speed Penalty"));
        addDouble(movementPenalty, eb, Component.literal("Base Movement Penalty"), CommonConfig.Specs.BASE_MOVEMENT_PENALTY, CommonConfig.COMMON.baseMovementPenalty, v -> CommonConfig.COMMON.baseMovementPenalty = v);
        addDouble(movementPenalty, eb, Component.literal("Weight Penalty Multiplier"), CommonConfig.Specs.WEIGHT_PENALTY_MULTIPLIER, CommonConfig.COMMON.weightPenaltyMultiplier, v -> CommonConfig.COMMON.weightPenaltyMultiplier = v);
        addDouble(movementPenalty, eb, Component.literal("Tension Penalty Multiplier"), CommonConfig.Specs.TENSION_PENALTY_MULTIPLIER, CommonConfig.COMMON.tensionPenaltyMultiplier, v -> CommonConfig.COMMON.tensionPenaltyMultiplier = v);
        addDouble(movementPenalty, eb, Component.literal("Kinetic Penalty Multiplier"), CommonConfig.Specs.KINETIC_PENALTY_MULTIPLIER, CommonConfig.COMMON.kineticPenaltyMultiplier, v -> CommonConfig.COMMON.kineticPenaltyMultiplier = v);
        addDouble(movementPenalty, eb, Component.literal("Min Speed While Grabbing"), CommonConfig.Specs.MIN_SPEED_WHILE_GRABBING, CommonConfig.COMMON.minSpeedWhileGrabbing, v -> CommonConfig.COMMON.minSpeedWhileGrabbing = v);
        physics.addEntry(movementPenalty.build());

        // Client Settings Subcategories
        SubCategoryBuilder rotInput = eb.startSubCategory(Component.literal("Rotation Input"));
        addDouble(rotInput, eb, Component.literal("Vertical Sensitivity"), CommonConfig.Specs.VERTICAL_ROTATION_SENSITIVITY, CommonConfig.CLIENT.verticalRotationSensitivity, v -> CommonConfig.CLIENT.verticalRotationSensitivity = v);
        addDouble(rotInput, eb, Component.literal("Horizontal Sensitivity"), CommonConfig.Specs.HORIZONTAL_ROTATION_SENSITIVITY, CommonConfig.CLIENT.horizontalRotationSensitivity, v -> CommonConfig.CLIENT.horizontalRotationSensitivity = v);
        addBoolean(rotInput, eb, Component.literal("Invert Vertical"), CommonConfig.Specs.INVERT_VERTICAL_ROTATION, CommonConfig.CLIENT.invertVerticalRotation, v -> CommonConfig.CLIENT.invertVerticalRotation = v);
        addBoolean(rotInput, eb, Component.literal("Invert Horizontal"), CommonConfig.Specs.INVERT_HORIZONTAL_ROTATION, CommonConfig.CLIENT.invertHorizontalRotation, v -> CommonConfig.CLIENT.invertHorizontalRotation = v);
        addBoolean(rotInput, eb, Component.literal("Rotate Around Center"), CommonConfig.Specs.ROTATE_AROUND_CENTER, CommonConfig.CLIENT.rotateAroundCenter, v -> CommonConfig.CLIENT.rotateAroundCenter = v);
        addBoolean(rotInput, eb, Component.literal("Prevent Movement While Rotating"), CommonConfig.Specs.PREVENT_MOVEMENT_WHILE_ROTATING, CommonConfig.CLIENT.preventMovementWhileRotating, v -> CommonConfig.CLIENT.preventMovementWhileRotating = v);
        client.addEntry(rotInput.build());

        SubCategoryBuilder armRendering = eb.startSubCategory(Component.literal("Arm & HUD Rendering"));
        addDouble(armRendering, eb, Component.literal("Arm Transition Speed"), CommonConfig.Specs.ARM_TRANSITION_SPEED, CommonConfig.CLIENT.armTransitionSpeed, v -> CommonConfig.CLIENT.armTransitionSpeed = v);
        addDouble(armRendering, eb, Component.literal("Arm Grab Lower Offset (m)"), CommonConfig.Specs.ARM_GRAB_LOWER_OFFSET, CommonConfig.CLIENT.armGrabLowerOffset, v -> CommonConfig.CLIENT.armGrabLowerOffset = v);
        addDouble(armRendering, eb, Component.literal("Assembly Shake Multiplier"), CommonConfig.Specs.ASSEMBLY_SHAKE_MULTIPLIER, CommonConfig.CLIENT.assemblyShakeMultiplier, v -> CommonConfig.CLIENT.assemblyShakeMultiplier = v);
        addDouble(armRendering, eb, Component.literal("Shake Frequency X"), CommonConfig.Specs.SHAKE_FREQUENCY_X, CommonConfig.CLIENT.shakeFrequencyX, v -> CommonConfig.CLIENT.shakeFrequencyX = v);
        addDouble(armRendering, eb, Component.literal("Shake Frequency Y"), CommonConfig.Specs.SHAKE_FREQUENCY_Y, CommonConfig.CLIENT.shakeFrequencyY, v -> CommonConfig.CLIENT.shakeFrequencyY = v);
        addDouble(armRendering, eb, Component.literal("Shake Frequency Z"), CommonConfig.Specs.SHAKE_FREQUENCY_Z, CommonConfig.CLIENT.shakeFrequencyZ, v -> CommonConfig.CLIENT.shakeFrequencyZ = v);
        addDouble(armRendering, eb, Component.literal("Arm Ease Full Threshold"), CommonConfig.Specs.ARM_EASE_FULL_THRESHOLD, CommonConfig.CLIENT.armEaseFullThreshold, v -> CommonConfig.CLIENT.armEaseFullThreshold = v);
        addDouble(armRendering, eb, Component.literal("Visual Shake Threshold"), CommonConfig.Specs.VISUAL_SHAKE_THRESHOLD, CommonConfig.CLIENT.visualShakeThreshold, v -> CommonConfig.CLIENT.visualShakeThreshold = v);
        addBoolean(armRendering, eb, Component.literal("Hide Hands While Grabbing"), CommonConfig.Specs.HIDE_HANDS_WHILE_GRABBING, CommonConfig.CLIENT.hideHandsWhileGrabbing, v -> CommonConfig.CLIENT.hideHandsWhileGrabbing = v);
        addDouble(armRendering, eb, Component.literal("Grab Arm Offset X"), CommonConfig.Specs.GRAB_ARM_OFFSET_X, CommonConfig.CLIENT.grabArmOffsetX, v -> CommonConfig.CLIENT.grabArmOffsetX = v);
        addDouble(armRendering, eb, Component.literal("Grab Arm Offset Y"), CommonConfig.Specs.GRAB_ARM_OFFSET_Y, CommonConfig.CLIENT.grabArmOffsetY, v -> CommonConfig.CLIENT.grabArmOffsetY = v);
        addDouble(armRendering, eb, Component.literal("Grab Arm Offset Z"), CommonConfig.Specs.GRAB_ARM_OFFSET_Z, CommonConfig.CLIENT.grabArmOffsetZ, v -> CommonConfig.CLIENT.grabArmOffsetZ = v);
        client.addEntry(armRendering.build());

        SubCategoryBuilder interaction = eb.startSubCategory(Component.literal("Interaction"));
        addBoolean(interaction, eb, Component.literal("Prevent Assembly When Mining"), CommonConfig.Specs.PREVENT_ASSEMBLY_WHEN_MINING, CommonConfig.CLIENT.preventAssemblyWhenMining, v -> CommonConfig.CLIENT.preventAssemblyWhenMining = v);
        addDouble(interaction, eb, Component.literal("Assembly Mining Threshold"), CommonConfig.Specs.BAREHANDED_ASSEMBLY_MINING_THRESHOLD, CommonConfig.CLIENT.barehandedAssemblyMiningThreshold, v -> CommonConfig.CLIENT.barehandedAssemblyMiningThreshold = v);
        addInt(interaction, eb, Component.literal("Regrab Debounce Ticks"), CommonConfig.Specs.REGRAB_DEBOUNCE_TICKS, CommonConfig.CLIENT.regrabDebounceTicks, v -> CommonConfig.CLIENT.regrabDebounceTicks = v);
        client.addEntry(interaction.build());

        return builder.build();
    }

    private static void addDouble(SubCategoryBuilder sub, ConfigEntryBuilder eb, Component title, CommonConfig.DoubleSpec spec, double current, java.util.function.Consumer<Double> save) {
        var b = eb.startDoubleField(title, current).setDefaultValue(spec.def()).setMin(spec.min()).setMax(spec.max()).setSaveConsumer(save);
        if (spec.tooltip() != null) b.setTooltip(Component.literal(spec.tooltip()));
        sub.add(b.build());
    }

    private static void addInt(SubCategoryBuilder sub, ConfigEntryBuilder eb, Component title, CommonConfig.IntSpec spec, int current, java.util.function.Consumer<Integer> save) {
        var b = eb.startIntField(title, current).setDefaultValue(spec.def()).setMin(spec.min()).setMax(spec.max()).setSaveConsumer(save);
        if (spec.tooltip() != null) b.setTooltip(Component.literal(spec.tooltip()));
        sub.add(b.build());
    }

    private static void addBoolean(SubCategoryBuilder sub, ConfigEntryBuilder eb, Component title, CommonConfig.BooleanSpec spec, boolean current, java.util.function.Consumer<Boolean> save) {
        var b = eb.startBooleanToggle(title, current).setDefaultValue(spec.def()).setSaveConsumer(save);
        if (spec.tooltip() != null) b.setTooltip(Component.literal(spec.tooltip()));
        sub.add(b.build());
    }
}