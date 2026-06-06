package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.CommonConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FabricConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Sable Barehanded"));

        builder.setSavingRunnable(CommonConfig::save);

        ConfigCategory physics = builder.getOrCreateCategory(Component.literal("Server Settings"));
        ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client Settings"));
        ConfigEntryBuilder eb = builder.entryBuilder();

        SubCategoryBuilder core = eb.startSubCategory(Component.literal("Core Grab Mechanics"));
        core.add(eb.startDoubleField(Component.literal("Max Force (N)"), CommonConfig.COMMON.maxForce)
                .setDefaultValue(120.0).setMin(1.0).setTooltip(Component.literal("Maximum spring force applied to move a grabbed object."))
                .setSaveConsumer(v -> CommonConfig.COMMON.maxForce = v).build());
        core.add(eb.startDoubleField(Component.literal("Min Distance (m)"), CommonConfig.COMMON.minDistance)
                .setDefaultValue(2.0).setMin(0.1).setTooltip(Component.literal("Minimum hold distance from the player's eye to the anchor."))
                .setSaveConsumer(v -> CommonConfig.COMMON.minDistance = v).build());
        core.add(eb.startDoubleField(Component.literal("Grab Reach Bonus (m)"), CommonConfig.COMMON.grabReachBonus)
                .setDefaultValue(2.0).setMin(0.0).setTooltip(Component.literal("Extra reach added on top of the block-interaction-range attribute."))
                .setSaveConsumer(v -> CommonConfig.COMMON.grabReachBonus = v).build());
        core.add(eb.startDoubleField(Component.literal("Grab Stabilization"), CommonConfig.COMMON.grabStabilization)
                .setDefaultValue(0.01).setMin(0.0).setMax(1.0).setTooltip(Component.literal("Positional stabilization factor (0 = loose, 1 = rigid)."))
                .setSaveConsumer(v -> CommonConfig.COMMON.grabStabilization = v).build());
        core.add(eb.startBooleanToggle(Component.literal("Creative Super Strength"), CommonConfig.COMMON.creativeSuperStrength)
                .setDefaultValue(true).setTooltip(Component.literal("Creative players ignore weight limits and get boosted physics."))
                .setSaveConsumer(v -> CommonConfig.COMMON.creativeSuperStrength = v).build());
        core.add(eb.startDoubleField(Component.literal("Strength I Multiplier"), CommonConfig.COMMON.strength1Multiplier)
                .setDefaultValue(2.0).setMin(1.0).setSaveConsumer(v -> CommonConfig.COMMON.strength1Multiplier = v).build());
        core.add(eb.startDoubleField(Component.literal("Strength II Multiplier"), CommonConfig.COMMON.strength2Multiplier)
                .setDefaultValue(4.0).setMin(1.0).setSaveConsumer(v -> CommonConfig.COMMON.strength2Multiplier = v).build());
        physics.addEntry(core.build());

        SubCategoryBuilder rotation = eb.startSubCategory(Component.literal("Rotation Mechanics"));
        rotation.add(eb.startBooleanToggle(Component.literal("Enable Rotation"), CommonConfig.COMMON.enableRotation)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.COMMON.enableRotation = v).build());
        rotation.add(eb.startDoubleField(Component.literal("Rotation Stabilization"), CommonConfig.COMMON.rotationStabilization)
                .setDefaultValue(0.5).setMin(0.0).setMax(1.0).setSaveConsumer(v -> CommonConfig.COMMON.rotationStabilization = v).build());
        rotation.add(eb.startDoubleField(Component.literal("Max Rotation Speed"), CommonConfig.COMMON.maxRotationSpeed)
                .setDefaultValue(0.2).setMin(0.0).setMax(1.0).setSaveConsumer(v -> CommonConfig.COMMON.maxRotationSpeed = v).build());
        rotation.add(eb.startBooleanToggle(Component.literal("Prevent Fast Rotations"), CommonConfig.COMMON.preventFastRotations)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.COMMON.preventFastRotations = v).build());
        rotation.add(eb.startDoubleField(Component.literal("Rotation Mass Damping Factor"), CommonConfig.COMMON.rotationMassDampingFactor)
                .setDefaultValue(0.02).setMin(0.0).setMax(1.0).setTooltip(Component.literal("How much object mass slows down rotation."))
                .setSaveConsumer(v -> CommonConfig.COMMON.rotationMassDampingFactor = v).build());
        rotation.add(eb.startIntField(Component.literal("Rotation Ticks Window"), CommonConfig.COMMON.rotationTicksWindow)
                .setDefaultValue(5).setMin(1).setMax(40).setTooltip(Component.literal("Ticks the rotation motor stays active after the last mouse input."))
                .setSaveConsumer(v -> CommonConfig.COMMON.rotationTicksWindow = v).build());
        rotation.add(eb.startDoubleField(Component.literal("Rotation Rebuild Threshold (rad)"), CommonConfig.COMMON.rotationRebuildThreshold)
                .setDefaultValue(0.25).setMin(0.01).setMax(Math.PI).setTooltip(Component.literal("Angle (radians) that triggers a constraint pivot rebuild."))
                .setSaveConsumer(v -> CommonConfig.COMMON.rotationRebuildThreshold = v).build());
        physics.addEntry(rotation.build());

        SubCategoryBuilder assembly = eb.startSubCategory(Component.literal("Block Detach & Pull Mechanics"));
        assembly.add(eb.startBooleanToggle(Component.literal("Enable Assembly"), CommonConfig.COMMON.enableBarehandedAssembly)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.COMMON.enableBarehandedAssembly = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Detach Speed Multiplier"), CommonConfig.COMMON.barehandedAssemblySpeedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setSaveConsumer(v -> CommonConfig.COMMON.barehandedAssemblySpeedMultiplier = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Detach Max Distance (m)"), CommonConfig.COMMON.barehandedAssemblyMaxDistance)
                .setDefaultValue(2.0).setMin(1.0).setMax(10.0).setSaveConsumer(v -> CommonConfig.COMMON.barehandedAssemblyMaxDistance = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Server Distance Tolerance (m)"), CommonConfig.COMMON.assemblyServerDistanceTolerance)
                .setDefaultValue(1.0).setMin(0.0).setMax(10.0).setTooltip(Component.literal("Server-side grace distance added when validating assembly requests."))
                .setSaveConsumer(v -> CommonConfig.COMMON.assemblyServerDistanceTolerance = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Client Distance Tolerance (m)"), CommonConfig.COMMON.assemblyClientDistanceTolerance)
                .setDefaultValue(1.5).setMin(0.0).setMax(10.0).setTooltip(Component.literal("Client-side grace distance before cancelling the charge."))
                .setSaveConsumer(v -> CommonConfig.COMMON.assemblyClientDistanceTolerance = v).build());
        assembly.add(eb.startIntField(Component.literal("Fast-Lift Assembly Ticks"), CommonConfig.COMMON.fastLiftAssemblyTicks)
                .setDefaultValue(2).setMin(1).setMax(200).setTooltip(Component.literal("Charge ticks for blocks with a BlockEntity but non-full collision (chests, barrels...)."))
                .setSaveConsumer(v -> CommonConfig.COMMON.fastLiftAssemblyTicks = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Pull Threshold (m)"), CommonConfig.COMMON.pullThreshold)
                .setDefaultValue(0.05).setMin(0.0).setMax(1.0).setSaveConsumer(v -> CommonConfig.COMMON.pullThreshold = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Pull Resistance Multiplier"), CommonConfig.COMMON.pullResistanceMultiplier)
                .setDefaultValue(0.6).setMin(0.0).setMax(5.0).setSaveConsumer(v -> CommonConfig.COMMON.pullResistanceMultiplier = v).build());
        assembly.add(eb.startDoubleField(Component.literal("Movement Damping"), CommonConfig.COMMON.assemblyMovementDamping)
                .setDefaultValue(0.5).setMin(0.0).setMax(1.0).setSaveConsumer(v -> CommonConfig.COMMON.assemblyMovementDamping = v).build());
        physics.addEntry(assembly.build());

        SubCategoryBuilder colls = eb.startSubCategory(Component.literal("Collision Filters"));
        colls.add(eb.startBooleanToggle(Component.literal("[Grab] Ignore Self"), CommonConfig.COMMON.ignoreCollisionsGrabSelf)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsGrabSelf = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Grab] Ignore Other Players"), CommonConfig.COMMON.ignoreCollisionsGrabOtherPlayers)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsGrabOtherPlayers = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Grab] Ignore Entities"), CommonConfig.COMMON.ignoreCollisionsGrabEntities)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsGrabEntities = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Grab] Ignore Everything"), CommonConfig.COMMON.ignoreCollisionsGrabEverything)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsGrabEverything = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Rotate] Ignore Self"), CommonConfig.COMMON.ignoreCollisionsRotationSelf)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsRotationSelf = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Rotate] Ignore Other Players"), CommonConfig.COMMON.ignoreCollisionsRotationOtherPlayers)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsRotationOtherPlayers = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Rotate] Ignore Entities"), CommonConfig.COMMON.ignoreCollisionsRotationEntities)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsRotationEntities = v).build());
        colls.add(eb.startBooleanToggle(Component.literal("[Rotate] Ignore Everything"), CommonConfig.COMMON.ignoreCollisionsRotationEverything)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.COMMON.ignoreCollisionsRotationEverything = v).build());
        physics.addEntry(colls.build());

        SubCategoryBuilder suspension = eb.startSubCategory(Component.literal("Physics Suspension"));
        suspension.add(eb.startIntField(Component.literal("Standing-on-Grab Suspend Ticks"), CommonConfig.COMMON.standingOnGrabSuspendTicks)
                .setDefaultValue(15).setMin(0).setMax(100).setTooltip(Component.literal("Ticks physics stays suspended after stepping off a grabbed object."))
                .setSaveConsumer(v -> CommonConfig.COMMON.standingOnGrabSuspendTicks = v).build());
        suspension.add(eb.startDoubleField(Component.literal("Proximity Eye Suspend Distance (m)"), CommonConfig.COMMON.grabProximityEyeSuspendDistance)
                .setDefaultValue(1.0).setMin(0.0).setMax(10.0).setTooltip(Component.literal("Eye-to-block distance that suspends physics to prevent clipping."))
                .setSaveConsumer(v -> CommonConfig.COMMON.grabProximityEyeSuspendDistance = v).build());
        suspension.add(eb.startDoubleField(Component.literal("Proximity Body Suspend Distance (m)"), CommonConfig.COMMON.grabProximityBodySuspendDistance)
                .setDefaultValue(1.5).setMin(0.0).setMax(10.0).setTooltip(Component.literal("Foot-to-block distance that suspends physics to prevent clipping."))
                .setSaveConsumer(v -> CommonConfig.COMMON.grabProximityBodySuspendDistance = v).build());
        suspension.add(eb.startDoubleField(Component.literal("Tension Suspend Threshold"), CommonConfig.COMMON.tensionSuspendThreshold)
                .setDefaultValue(3.5).setMin(0.0).setSaveConsumer(v -> CommonConfig.COMMON.tensionSuspendThreshold = v).build());
        suspension.add(eb.startDoubleField(Component.literal("Tension Break Threshold"), CommonConfig.COMMON.tensionBreakThreshold)
                .setDefaultValue(9.0).setMin(0.0).setSaveConsumer(v -> CommonConfig.COMMON.tensionBreakThreshold = v).build());
        suspension.add(eb.startDoubleField(Component.literal("Creative Tension Suspend Threshold"), CommonConfig.COMMON.creativeTensionSuspendThreshold)
                .setDefaultValue(64.0).setMin(0.0).setTooltip(Component.literal("Tension suspend threshold in Creative Super Strength mode."))
                .setSaveConsumer(v -> CommonConfig.COMMON.creativeTensionSuspendThreshold = v).build());
        suspension.add(eb.startDoubleField(Component.literal("Creative Tension Break Threshold"), CommonConfig.COMMON.creativeTensionBreakThreshold)
                .setDefaultValue(64.0).setMin(0.0).setTooltip(Component.literal("Tension break threshold in Creative Super Strength mode."))
                .setSaveConsumer(v -> CommonConfig.COMMON.creativeTensionBreakThreshold = v).build());
        physics.addEntry(suspension.build());

        SubCategoryBuilder velocity = eb.startSubCategory(Component.literal("Player Velocity Limits"));
        velocity.add(eb.startDoubleField(Component.literal("Max Velocity Y Up (m/t)"), CommonConfig.COMMON.maxPlayerVelocityYUp)
                .setDefaultValue(2.5).setMin(0.0).setMax(10.0).setSaveConsumer(v -> CommonConfig.COMMON.maxPlayerVelocityYUp = v).build());
        velocity.add(eb.startDoubleField(Component.literal("Max Velocity Y Down (m/t)"), CommonConfig.COMMON.maxPlayerVelocityYDown)
                .setDefaultValue(-4.0).setMin(-20.0).setMax(0.0).setSaveConsumer(v -> CommonConfig.COMMON.maxPlayerVelocityYDown = v).build());
        velocity.add(eb.startDoubleField(Component.literal("Max Velocity XZ (m/t)"), CommonConfig.COMMON.maxPlayerVelocityXZ)
                .setDefaultValue(2.5).setMin(0.0).setMax(10.0).setSaveConsumer(v -> CommonConfig.COMMON.maxPlayerVelocityXZ = v).build());
        physics.addEntry(velocity.build());

        SubCategoryBuilder lead = eb.startSubCategory(Component.literal("Player Lead (Predictive Offset)"));
        lead.add(eb.startDoubleField(Component.literal("Speed Threshold (m/t)"), CommonConfig.COMMON.playerSpeedLeadThreshold)
                .setDefaultValue(0.1).setMin(0.0).setMax(2.0).setTooltip(Component.literal("Minimum player speed before a lead offset is applied."))
                .setSaveConsumer(v -> CommonConfig.COMMON.playerSpeedLeadThreshold = v).build());
        lead.add(eb.startDoubleField(Component.literal("Lead Multiplier"), CommonConfig.COMMON.playerSpeedLeadMultiplier)
                .setDefaultValue(2.0).setMin(0.0).setMax(10.0).setTooltip(Component.literal("Velocity multiplier for the predictive anchor offset."))
                .setSaveConsumer(v -> CommonConfig.COMMON.playerSpeedLeadMultiplier = v).build());
        lead.add(eb.startDoubleField(Component.literal("Y Down Lead Cap"), CommonConfig.COMMON.playerSpeedLeadYDownCap)
                .setDefaultValue(-0.5).setMin(-10.0).setMax(0.0).setTooltip(Component.literal("Maximum downward component of the lead offset (negative)."))
                .setSaveConsumer(v -> CommonConfig.COMMON.playerSpeedLeadYDownCap = v).build());
        physics.addEntry(lead.build());

        SubCategoryBuilder advanced = eb.startSubCategory(Component.literal("Advanced Physics Tuning"));
        advanced.add(eb.startDoubleField(Component.literal("Stiffness"), CommonConfig.COMMON.stiffness)
                .setDefaultValue(1000.0).setMin(1.0).setSaveConsumer(v -> CommonConfig.COMMON.stiffness = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Damping"), CommonConfig.COMMON.damping)
                .setDefaultValue(125.0).setMin(1.0).setSaveConsumer(v -> CommonConfig.COMMON.damping = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Angular Damping"), CommonConfig.COMMON.angularDamping)
                .setDefaultValue(850.0).setMin(1.0).setSaveConsumer(v -> CommonConfig.COMMON.angularDamping = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Creative Strength Multiplier"), CommonConfig.COMMON.creativeStrengthMultiplier)
                .setDefaultValue(10.0).setMin(1.0).setTooltip(Component.literal("Multiplies stiffness, damping and force in Creative Super Strength mode."))
                .setSaveConsumer(v -> CommonConfig.COMMON.creativeStrengthMultiplier = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Speed Stiffness Factor"), CommonConfig.COMMON.speedStiffnessMultiplierFactor)
                .setDefaultValue(15.0).setMin(0.0).setTooltip(Component.literal("Factor scaling spring stiffness with player speed."))
                .setSaveConsumer(v -> CommonConfig.COMMON.speedStiffnessMultiplierFactor = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Max Speed Stiffness Mult"), CommonConfig.COMMON.maxSpeedStiffnessMultiplier)
                .setDefaultValue(8.0).setMin(1.0).setTooltip(Component.literal("Cap on the speed-based stiffness multiplier."))
                .setSaveConsumer(v -> CommonConfig.COMMON.maxSpeedStiffnessMultiplier = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Base Angular Force Factor"), CommonConfig.COMMON.baseAngularForceFactor)
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0).setTooltip(Component.literal("Fraction of maxForce used as minimum angular force."))
                .setSaveConsumer(v -> CommonConfig.COMMON.baseAngularForceFactor = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Stable Angular Force: Mass Base"), CommonConfig.COMMON.stableAngularForceMassBase)
                .setDefaultValue(10.0).setMin(0.0).setTooltip(Component.literal("Constant term: stableForce = maxForce * (base + mass * factor)."))
                .setSaveConsumer(v -> CommonConfig.COMMON.stableAngularForceMassBase = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Stable Angular Force: Mass Factor"), CommonConfig.COMMON.stableAngularForceMassFactor)
                .setDefaultValue(0.5).setMin(0.0).setTooltip(Component.literal("Mass coefficient in the stable angular force formula."))
                .setSaveConsumer(v -> CommonConfig.COMMON.stableAngularForceMassFactor = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Rotating Angular Stiffness: Base"), CommonConfig.COMMON.rotatingAngularStiffnessBase)
                .setDefaultValue(1.5).setMin(0.0).setTooltip(Component.literal("Base angular stiffness multiplier while actively rotating."))
                .setSaveConsumer(v -> CommonConfig.COMMON.rotatingAngularStiffnessBase = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Rotating Angular Stiffness: Range"), CommonConfig.COMMON.rotatingAngularStiffnessRange)
                .setDefaultValue(4.5).setMin(0.0).setTooltip(Component.literal("Range component of the rotation stiffness multiplier."))
                .setSaveConsumer(v -> CommonConfig.COMMON.rotatingAngularStiffnessRange = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Sway Angular Stiffness: Base"), CommonConfig.COMMON.swayAngularStiffnessBase)
                .setDefaultValue(0.6).setMin(0.0).setTooltip(Component.literal("Base angular stiffness multiplier when idle (sway mode)."))
                .setSaveConsumer(v -> CommonConfig.COMMON.swayAngularStiffnessBase = v).build());
        advanced.add(eb.startDoubleField(Component.literal("Sway Angular Stiffness: Range"), CommonConfig.COMMON.swayAngularStiffnessRange)
                .setDefaultValue(5.4).setMin(0.0).setTooltip(Component.literal("Range component of the idle sway stiffness multiplier."))
                .setSaveConsumer(v -> CommonConfig.COMMON.swayAngularStiffnessRange = v).build());
        physics.addEntry(advanced.build());

        SubCategoryBuilder rotInput = eb.startSubCategory(Component.literal("Rotation Input"));
        rotInput.add(eb.startDoubleField(Component.literal("Vertical Sensitivity"), CommonConfig.CLIENT.verticalRotationSensitivity)
                .setDefaultValue(0.5).setMin(0.1).setMax(1.0).setSaveConsumer(v -> CommonConfig.CLIENT.verticalRotationSensitivity = v).build());
        rotInput.add(eb.startDoubleField(Component.literal("Horizontal Sensitivity"), CommonConfig.CLIENT.horizontalRotationSensitivity)
                .setDefaultValue(0.5).setMin(0.1).setMax(1.0).setSaveConsumer(v -> CommonConfig.CLIENT.horizontalRotationSensitivity = v).build());
        rotInput.add(eb.startBooleanToggle(Component.literal("Invert Vertical"), CommonConfig.CLIENT.invertVerticalRotation)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.CLIENT.invertVerticalRotation = v).build());
        rotInput.add(eb.startBooleanToggle(Component.literal("Invert Horizontal"), CommonConfig.CLIENT.invertHorizontalRotation)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.CLIENT.invertHorizontalRotation = v).build());
        rotInput.add(eb.startBooleanToggle(Component.literal("Rotate Around Center"), CommonConfig.CLIENT.rotateAroundCenter)
                .setDefaultValue(false).setSaveConsumer(v -> CommonConfig.CLIENT.rotateAroundCenter = v).build());
        rotInput.add(eb.startBooleanToggle(Component.literal("Prevent Movement While Rotating"), CommonConfig.CLIENT.preventMovementWhileRotating)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.CLIENT.preventMovementWhileRotating = v).build());
        client.addEntry(rotInput.build());

        SubCategoryBuilder armRendering = eb.startSubCategory(Component.literal("Arm & HUD Rendering"));
        armRendering.add(eb.startDoubleField(Component.literal("Arm Transition Speed"), CommonConfig.CLIENT.armTransitionSpeed)
                .setDefaultValue(0.2).setMin(0.01).setMax(1.0).setTooltip(Component.literal("Per-tick speed of the arm hide/show transition."))
                .setSaveConsumer(v -> CommonConfig.CLIENT.armTransitionSpeed = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Arm Grab Lower Offset (m)"), CommonConfig.CLIENT.armGrabLowerOffset)
                .setDefaultValue(1.5).setMin(0.0).setMax(5.0).setTooltip(Component.literal("How far the vanilla arm is pushed down during partial transitions."))
                .setSaveConsumer(v -> CommonConfig.CLIENT.armGrabLowerOffset = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Assembly Shake Multiplier"), CommonConfig.CLIENT.assemblyShakeMultiplier)
                .setDefaultValue(0.04).setMin(0.0).setMax(1.0).setTooltip(Component.literal("Camera shake scale while charging a block detach."))
                .setSaveConsumer(v -> CommonConfig.CLIENT.assemblyShakeMultiplier = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Shake Frequency X"), CommonConfig.CLIENT.shakeFrequencyX)
                .setDefaultValue(3.0).setMin(0.1).setMax(20.0).setSaveConsumer(v -> CommonConfig.CLIENT.shakeFrequencyX = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Shake Frequency Y"), CommonConfig.CLIENT.shakeFrequencyY)
                .setDefaultValue(4.0).setMin(0.1).setMax(20.0).setSaveConsumer(v -> CommonConfig.CLIENT.shakeFrequencyY = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Shake Frequency Z"), CommonConfig.CLIENT.shakeFrequencyZ)
                .setDefaultValue(5.0).setMin(0.1).setMax(20.0).setSaveConsumer(v -> CommonConfig.CLIENT.shakeFrequencyZ = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Arm Ease Full Threshold"), CommonConfig.CLIENT.armEaseFullThreshold)
                .setDefaultValue(0.99).setMin(0.5).setMax(1.0).setTooltip(Component.literal("Ease progress above which the vanilla arm is fully hidden."))
                .setSaveConsumer(v -> CommonConfig.CLIENT.armEaseFullThreshold = v).build());
        armRendering.add(eb.startDoubleField(Component.literal("Visual Shake Threshold"), CommonConfig.CLIENT.visualShakeThreshold)
                .setDefaultValue(0.3).setMin(0.0).setMax(1.0).setTooltip(Component.literal("Minimum charge progress before the shake effect appears."))
                .setSaveConsumer(v -> CommonConfig.CLIENT.visualShakeThreshold = v).build());
        client.addEntry(armRendering.build());

        SubCategoryBuilder mining = eb.startSubCategory(Component.literal("Mining Prevention"));
        mining.add(eb.startBooleanToggle(Component.literal("Prevent Assembly When Mining"), CommonConfig.CLIENT.preventAssemblyWhenMining)
                .setDefaultValue(true).setSaveConsumer(v -> CommonConfig.CLIENT.preventAssemblyWhenMining = v).build());
        mining.add(eb.startDoubleField(Component.literal("Assembly Mining Threshold"), CommonConfig.CLIENT.barehandedAssemblyMiningThreshold)
                .setDefaultValue(0.05).setMin(0.0).setMax(1.0).setTooltip(Component.literal("Mining progress above which assembly is blocked."))
                .setSaveConsumer(v -> CommonConfig.CLIENT.barehandedAssemblyMiningThreshold = v).build());
        client.addEntry(mining.build());

        return builder.build();
    }
}