package dev.juaanp.sablebarehanded.client;

import dev.juaanp.sablebarehanded.config.ClientConfig;
import dev.juaanp.sablebarehanded.config.ServerConfig;
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

        builder.setSavingRunnable(() -> {
            ServerConfig.save();
            ClientConfig.save();
        });

        ConfigCategory server = builder.getOrCreateCategory(Component.literal("Server Settings"));
        ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client Settings"));
        ConfigEntryBuilder eb = builder.entryBuilder();

        buildServerCategories(server, eb);
        buildClientCategories(client, eb);

        return builder.build();
    }

    private static void buildServerCategories(ConfigCategory server, ConfigEntryBuilder eb) {
        SubCategoryBuilder core = eb.startSubCategory(Component.literal("Core Grab Mechanics"));
        addDouble(core, eb, "Max Force (N)", "Maximum spring force applied to move a grabbed object.", 1.0, 1000000.0, ServerConfig.INSTANCE.maxForce, v -> ServerConfig.INSTANCE.maxForce = v);
        addDouble(core, eb, "Min Distance (m)", "Minimum hold distance from the player's eye to the anchor.", 0.1, 1024.0, ServerConfig.INSTANCE.minDistance, v -> ServerConfig.INSTANCE.minDistance = v);
        addDouble(core, eb, "Grab Reach Bonus (m)", "Extra reach added on top of the block-interaction-range attribute.", 0.0, 1024.0, ServerConfig.INSTANCE.grabReachBonus, v -> ServerConfig.INSTANCE.grabReachBonus = v);
        addDouble(core, eb, "Grab Stabilization", "Positional stabilization factor (0 = loose, 1 = rigid).", 0.0, 1.0, ServerConfig.INSTANCE.grabStabilization, v -> ServerConfig.INSTANCE.grabStabilization = v);
        addBoolean(core, eb, "Creative Super Strength", "Creative players ignore weight limits and get boosted physics.", ServerConfig.INSTANCE.creativeSuperStrength, v -> ServerConfig.INSTANCE.creativeSuperStrength = v);
        addDouble(core, eb, "Strength I Multiplier", "Force multiplier when the player has Strength I effect.", 1.0, 10000.0, ServerConfig.INSTANCE.strength1Multiplier, v -> ServerConfig.INSTANCE.strength1Multiplier = v);
        addDouble(core, eb, "Strength II Multiplier", "Force multiplier when the player has Strength II (or higher) effect.", 1.0, 10000.0, ServerConfig.INSTANCE.strength2Multiplier, v -> ServerConfig.INSTANCE.strength2Multiplier = v);
        addDouble(core, eb, "Min Physics Mass", "Minimum mass required for an object to be grabbable (prevents grabbing tiny particles).", 0.0, 100.0, ServerConfig.INSTANCE.minPhysicsMass, v -> ServerConfig.INSTANCE.minPhysicsMass = v);
        server.addEntry(core.build());

        SubCategoryBuilder rotation = eb.startSubCategory(Component.literal("Rotation Mechanics"));
        addBoolean(rotation, eb, "Enable Rotation", "Allows players to rotate grabbed objects using the mouse.", ServerConfig.INSTANCE.enableRotation, v -> ServerConfig.INSTANCE.enableRotation = v);
        addDouble(rotation, eb, "Rotation Stabilization", "How rigidly the object holds its rotation when idle (0 = loose, 1 = rigid).", 0.0, 1.0, ServerConfig.INSTANCE.rotationStabilization, v -> ServerConfig.INSTANCE.rotationStabilization = v);
        addDouble(rotation, eb, "Max Rotation Speed", "Maximum allowed rotation speed per tick (radians).", 0.0, 3.14, ServerConfig.INSTANCE.maxRotationSpeed, v -> ServerConfig.INSTANCE.maxRotationSpeed = v);
        addBoolean(rotation, eb, "Prevent Fast Rotations", "Clamps rotation speed to prevent physics explosions from fast mouse flicks.", ServerConfig.INSTANCE.preventFastRotations, v -> ServerConfig.INSTANCE.preventFastRotations = v);
        addDouble(rotation, eb, "Rotation Mass Damping Factor", "How much object mass slows down rotation input.", 0.0, 100.0, ServerConfig.INSTANCE.rotationMassDampingFactor, v -> ServerConfig.INSTANCE.rotationMassDampingFactor = v);
        addInt(rotation, eb, "Rotation Ticks Window", "Ticks the rotation motor stays active after the last mouse input.", 1, 1200, ServerConfig.INSTANCE.rotationTicksWindow, v -> ServerConfig.INSTANCE.rotationTicksWindow = v);
        addDouble(rotation, eb, "Rotation Rebuild Threshold", "Angle (radians) that triggers a constraint pivot rebuild to prevent twisting.", 0.01, 3.14, ServerConfig.INSTANCE.rotationRebuildThreshold, v -> ServerConfig.INSTANCE.rotationRebuildThreshold = v);
        server.addEntry(rotation.build());

        SubCategoryBuilder assembly = eb.startSubCategory(Component.literal("Block Detach & Pull"));
        addBoolean(assembly, eb, "Enable Assembly", "Allows players to pull and detach connected blocks from the world.", ServerConfig.INSTANCE.enableBarehandedAssembly, v -> ServerConfig.INSTANCE.enableBarehandedAssembly = v);
        addDouble(assembly, eb, "Detach Speed Multiplier", "Multiplier for the time it takes to pull blocks out of the world.", 0.1, 1000.0, ServerConfig.INSTANCE.barehandedAssemblySpeedMultiplier, v -> ServerConfig.INSTANCE.barehandedAssemblySpeedMultiplier = v);
        addDouble(assembly, eb, "Detach Max Distance (m)", "Maximum distance from the player to start pulling blocks.", 1.0, 1024.0, ServerConfig.INSTANCE.barehandedAssemblyMaxDistance, v -> ServerConfig.INSTANCE.barehandedAssemblyMaxDistance = v);
        addDouble(assembly, eb, "Server Tolerance (m)", "Server-side grace distance added when validating assembly requests.", 0.0, 1024.0, ServerConfig.INSTANCE.assemblyServerDistanceTolerance, v -> ServerConfig.INSTANCE.assemblyServerDistanceTolerance = v);
        addDouble(assembly, eb, "Client Tolerance (m)", "Client-side grace distance before cancelling the pull charge.", 0.0, 1024.0, ServerConfig.INSTANCE.assemblyClientDistanceTolerance, v -> ServerConfig.INSTANCE.assemblyClientDistanceTolerance = v);
        addInt(assembly, eb, "Fast-Lift Ticks", "Charge ticks for blocks with a BlockEntity but non-full collision (chests, barrels...).", 1, 12000, ServerConfig.INSTANCE.fastLiftAssemblyTicks, v -> ServerConfig.INSTANCE.fastLiftAssemblyTicks = v);
        addDouble(assembly, eb, "Pull Threshold (m)", "Distance the player must pull back to advance the assembly charge.", 0.0, 100.0, ServerConfig.INSTANCE.pullThreshold, v -> ServerConfig.INSTANCE.pullThreshold = v);
        addDouble(assembly, eb, "Pull Resistance Multiplier", "How much the pulled object resists the player's movement.", 0.0, 1000.0, ServerConfig.INSTANCE.pullResistanceMultiplier, v -> ServerConfig.INSTANCE.pullResistanceMultiplier = v);
        addDouble(assembly, eb, "Movement Damping", "Dampens player movement while actively charging a detach.", 0.0, 1.0, ServerConfig.INSTANCE.assemblyMovementDamping, v -> ServerConfig.INSTANCE.assemblyMovementDamping = v);
        addDouble(assembly, eb, "Assembly Tether Stiffness", "Elastic stiffness when the player stretches beyond the max detach distance.", 0.0, 10.0, ServerConfig.INSTANCE.assemblyTetherStiffness, v -> ServerConfig.INSTANCE.assemblyTetherStiffness = v);
        addDouble(assembly, eb, "Assembly Max Stretch Buffer (m)", "Absolute maximum distance before the assembly charge forcefully breaks.", 0.0, 20.0, ServerConfig.INSTANCE.assemblyMaxStretchBuffer, v -> ServerConfig.INSTANCE.assemblyMaxStretchBuffer = v);
        server.addEntry(assembly.build());

        SubCategoryBuilder encumbrance = eb.startSubCategory(Component.literal("Encumbrance & Tethering"));
        addBoolean(encumbrance, eb, "Enable Encumbrance", "Enables player movement and camera penalties based on grabbed object mass.", ServerConfig.INSTANCE.enableEncumbrance, v -> ServerConfig.INSTANCE.enableEncumbrance = v);
        addDouble(encumbrance, eb, "Physics Gravity", "Gravity constant (m/s^2) used to convert mass into resting force (Weight = Mass * Gravity).", 0.1, 1000.0, ServerConfig.INSTANCE.physicsGravity, v -> ServerConfig.INSTANCE.physicsGravity = v);
        addDouble(encumbrance, eb, "Max Movement Penalty", "Maximum speed reduction (0.85 = 85% slower) when holding heavy objects.", 0.0, 1.0, ServerConfig.INSTANCE.maxMovementPenalty, v -> ServerConfig.INSTANCE.maxMovementPenalty = v);
        addDouble(encumbrance, eb, "Jump Prevention", "Encumbrance ratio (0.0 to 1.0) at which jumping is disabled.", 0.0, 1.0, ServerConfig.INSTANCE.jumpPreventionThreshold, v -> ServerConfig.INSTANCE.jumpPreventionThreshold = v);
        addDouble(encumbrance, eb, "Max Camera Penalty", "Maximum camera sensitivity reduction when holding heavy objects.", 0.0, 1.0, ServerConfig.INSTANCE.maxCameraPenalty, v -> ServerConfig.INSTANCE.maxCameraPenalty = v);
        addBoolean(encumbrance, eb, "Enable Tether", "Enables the physical pull-back when stretching arms beyond the limit.", ServerConfig.INSTANCE.enablePhysicalTether, v -> ServerConfig.INSTANCE.enablePhysicalTether = v);
        addDouble(encumbrance, eb, "Arm Stretch Tolerance", "Extra distance (m) arms can stretch before the tether pulls the player.", 0.0, 50.0, ServerConfig.INSTANCE.armStretchTolerance, v -> ServerConfig.INSTANCE.armStretchTolerance = v);
        addDouble(encumbrance, eb, "Tether Stiffness Base", "Base pull strength of the tether when stretched.", 0.0, 10.0, ServerConfig.INSTANCE.tetherStiffnessBase, v -> ServerConfig.INSTANCE.tetherStiffnessBase = v);
        addDouble(encumbrance, eb, "Tether Stiffness Multiplier", "Additional pull strength based on the object's encumbrance.", 0.0, 50.0, ServerConfig.INSTANCE.tetherStiffnessMultiplier, v -> ServerConfig.INSTANCE.tetherStiffnessMultiplier = v);
        addDouble(encumbrance, eb, "Tether Vertical Smoothing", "Reduces vertical yanking to prevent the player from being launched into the air.", 0.0, 1.0, ServerConfig.INSTANCE.tetherVerticalSmoothing, v -> ServerConfig.INSTANCE.tetherVerticalSmoothing = v);
        addDouble(encumbrance, eb, "Recoil Velocity Threshold", "Minimum away-velocity required for the object to apply a recoil/pullback force.", 0.0, 1.0, ServerConfig.INSTANCE.recoilVelocityThreshold, v -> ServerConfig.INSTANCE.recoilVelocityThreshold = v);
        addDouble(encumbrance, eb, "Tether Hard Escape Buffer", "Hard distance limit where the tether forcefully strips away-velocity to prevent escaping.", 0.0, 100.0, ServerConfig.INSTANCE.tetherHardEscapeBuffer, v -> ServerConfig.INSTANCE.tetherHardEscapeBuffer = v);
        server.addEntry(encumbrance.build());

        SubCategoryBuilder exhaustion = eb.startSubCategory(Component.literal("Hunger & Exhaustion"));
        addBoolean(exhaustion, eb, "Enable Exhaustion", "Consumes player hunger based on physical effort when grabbing objects.", ServerConfig.INSTANCE.enableExhaustion, v -> ServerConfig.INSTANCE.enableExhaustion = v);
        addDouble(exhaustion, eb, "Idle Drain Rate", "Exhaustion added per tick just by holding a heavy object in the air.", 0.0, 1.0, ServerConfig.INSTANCE.exhaustionIdleRate, v -> ServerConfig.INSTANCE.exhaustionIdleRate = v);
        addDouble(exhaustion, eb, "Movement Drain Rate", "Exhaustion multiplier based on player walking/jumping speed while holding weight.", 0.0, 1.0, ServerConfig.INSTANCE.exhaustionMovementRate, v -> ServerConfig.INSTANCE.exhaustionMovementRate = v);
        addDouble(exhaustion, eb, "Tension Drain Rate", "Extra exhaustion multiplier when pulling against a stuck heavy object.", 0.0, 1.0, ServerConfig.INSTANCE.exhaustionTensionRate, v -> ServerConfig.INSTANCE.exhaustionTensionRate = v);
        addDouble(exhaustion, eb, "Force Drain Rate", "Exhaustion multiplier based on kinetic energy (moving heavy blocks fast).", 0.0, 1.0, ServerConfig.INSTANCE.exhaustionForceRate, v -> ServerConfig.INSTANCE.exhaustionForceRate = v);
        addDouble(exhaustion, eb, "Passive Force Threshold", "Weight in Newtons that is considered 'free' to hold without any effort.", 0.0, 1000.0, ServerConfig.INSTANCE.exhaustionPassiveThreshold, v -> ServerConfig.INSTANCE.exhaustionPassiveThreshold = v);
        addDouble(exhaustion, eb, "Support Height Threshold", "Relative height below which the object is considered partially supported by the ground.", 0.0, 10.0, ServerConfig.INSTANCE.exhaustionSupportHeightThreshold, v -> ServerConfig.INSTANCE.exhaustionSupportHeightThreshold = v);
        addDouble(exhaustion, eb, "Low Support Multiplier", "Reduces exhaustion effort when the object is resting on the ground.", 0.0, 1.0, ServerConfig.INSTANCE.exhaustionLowSupportMultiplier, v -> ServerConfig.INSTANCE.exhaustionLowSupportMultiplier = v);
        addDouble(exhaustion, eb, "Max Over Stretch", "Maximum over-stretch distance factored into tension exhaustion.", 0.0, 100.0, ServerConfig.INSTANCE.exhaustionMaxOverStretch, v -> ServerConfig.INSTANCE.exhaustionMaxOverStretch = v);
        addDouble(exhaustion, eb, "Kinetic Reference Speed", "Block speed (m/t) at which kinetic exhaustion reaches its maximum.", 0.1, 100.0, ServerConfig.INSTANCE.exhaustionKineticReferenceSpeed, v -> ServerConfig.INSTANCE.exhaustionKineticReferenceSpeed = v);
        addDouble(exhaustion, eb, "Vertical Weight Factor", "Multiplier for vertical player movement (jumping) when calculating exhaustion.", 0.0, 100.0, ServerConfig.INSTANCE.exhaustionVerticalWeightFactor, v -> ServerConfig.INSTANCE.exhaustionVerticalWeightFactor = v);
        server.addEntry(exhaustion.build());

        SubCategoryBuilder suspension = eb.startSubCategory(Component.literal("Physics Suspension"));
        addInt(suspension, eb, "Standing-on-Grab Suspend Ticks", "Ticks physics stays suspended after stepping off a grabbed object.", 0, 12000, ServerConfig.INSTANCE.standingOnGrabSuspendTicks, v -> ServerConfig.INSTANCE.standingOnGrabSuspendTicks = v);
        addDouble(suspension, eb, "Proximity Eye Suspend Distance", "Eye-to-block distance that suspends physics to prevent clipping.", 0.0, 1024.0, ServerConfig.INSTANCE.grabProximityEyeSuspendDistance, v -> ServerConfig.INSTANCE.grabProximityEyeSuspendDistance = v);
        addDouble(suspension, eb, "Proximity Body Suspend Distance", "Foot-to-block distance that suspends physics to prevent clipping.", 0.0, 1024.0, ServerConfig.INSTANCE.grabProximityBodySuspendDistance, v -> ServerConfig.INSTANCE.grabProximityBodySuspendDistance = v);
        addDouble(suspension, eb, "Tension Suspend Threshold", "Tension distance that temporarily suspends physics motors to prevent jitter.", 0.0, 10000.0, ServerConfig.INSTANCE.tensionSuspendThreshold, v -> ServerConfig.INSTANCE.tensionSuspendThreshold = v);
        addDouble(suspension, eb, "Tension Break Threshold", "Tension distance that forcefully breaks the grab.", 0.0, 10000.0, ServerConfig.INSTANCE.tensionBreakThreshold, v -> ServerConfig.INSTANCE.tensionBreakThreshold = v);
        addDouble(suspension, eb, "Creative Tension Suspend", "Tension suspend threshold in Creative Super Strength mode.", 0.0, 10000.0, ServerConfig.INSTANCE.creativeTensionSuspendThreshold, v -> ServerConfig.INSTANCE.creativeTensionSuspendThreshold = v);
        addDouble(suspension, eb, "Creative Tension Break", "Tension break threshold in Creative Super Strength mode.", 0.0, 10000.0, ServerConfig.INSTANCE.creativeTensionBreakThreshold, v -> ServerConfig.INSTANCE.creativeTensionBreakThreshold = v);
        server.addEntry(suspension.build());

        SubCategoryBuilder velocity = eb.startSubCategory(Component.literal("Player Velocity Limits"));
        addDouble(velocity, eb, "Max Velocity Y Up (m/t)", "Maximum upward velocity allowed while grabbing.", 0.0, 1000.0, ServerConfig.INSTANCE.maxPlayerVelocityYUp, v -> ServerConfig.INSTANCE.maxPlayerVelocityYUp = v);
        addDouble(velocity, eb, "Max Velocity Y Down (m/t)", "Maximum downward velocity allowed while grabbing.", -1000.0, 0.0, ServerConfig.INSTANCE.maxPlayerVelocityYDown, v -> ServerConfig.INSTANCE.maxPlayerVelocityYDown = v);
        addDouble(velocity, eb, "Max Velocity XZ (m/t)", "Maximum horizontal velocity allowed while grabbing.", 0.0, 1000.0, ServerConfig.INSTANCE.maxPlayerVelocityXZ, v -> ServerConfig.INSTANCE.maxPlayerVelocityXZ = v);
        server.addEntry(velocity.build());

        SubCategoryBuilder colls = eb.startSubCategory(Component.literal("Collision Filters"));
        addBoolean(colls, eb, "[Grab] Ignore Self", "Prevents the grabbed object from colliding with the grabber.", ServerConfig.INSTANCE.ignoreCollisionsGrabSelf, v -> ServerConfig.INSTANCE.ignoreCollisionsGrabSelf = v);
        addBoolean(colls, eb, "[Grab] Ignore Other Players", "Prevents the grabbed object from colliding with other players.", ServerConfig.INSTANCE.ignoreCollisionsGrabOtherPlayers, v -> ServerConfig.INSTANCE.ignoreCollisionsGrabOtherPlayers = v);
        addBoolean(colls, eb, "[Grab] Ignore Entities", "Prevents the grabbed object from colliding with mobs/entities.", ServerConfig.INSTANCE.ignoreCollisionsGrabEntities, v -> ServerConfig.INSTANCE.ignoreCollisionsGrabEntities = v);
        addBoolean(colls, eb, "[Grab] Ignore Everything", "Makes the grabbed object completely non-collidable (Ghost mode).", ServerConfig.INSTANCE.ignoreCollisionsGrabEverything, v -> ServerConfig.INSTANCE.ignoreCollisionsGrabEverything = v);
        addDouble(colls, eb, "Self Collision Ignore Distance Sq", "Squared distance to force-ignore self-collision to prevent physics penetration loops.", 0.0, 1000.0, ServerConfig.INSTANCE.selfCollisionIgnoreDistanceSq, v -> ServerConfig.INSTANCE.selfCollisionIgnoreDistanceSq = v);
        addBoolean(colls, eb, "[Rotate] Ignore Self", "Prevents collisions with the grabber while actively rotating.", ServerConfig.INSTANCE.ignoreCollisionsRotationSelf, v -> ServerConfig.INSTANCE.ignoreCollisionsRotationSelf = v);
        addBoolean(colls, eb, "[Rotate] Ignore Other Players", "Prevents collisions with other players while rotating.", ServerConfig.INSTANCE.ignoreCollisionsRotationOtherPlayers, v -> ServerConfig.INSTANCE.ignoreCollisionsRotationOtherPlayers = v);
        addBoolean(colls, eb, "[Rotate] Ignore Entities", "Prevents collisions with mobs/entities while rotating.", ServerConfig.INSTANCE.ignoreCollisionsRotationEntities, v -> ServerConfig.INSTANCE.ignoreCollisionsRotationEntities = v);
        addBoolean(colls, eb, "[Rotate] Ignore Everything", "Makes the object completely non-collidable while rotating.", ServerConfig.INSTANCE.ignoreCollisionsRotationEverything, v -> ServerConfig.INSTANCE.ignoreCollisionsRotationEverything = v);
        server.addEntry(colls.build());

        SubCategoryBuilder advanced = eb.startSubCategory(Component.literal("Advanced Physics Tuning"));
        addDouble(advanced, eb, "Stiffness", "Base linear spring stiffness.", 1.0, 10000000.0, ServerConfig.INSTANCE.stiffness, v -> ServerConfig.INSTANCE.stiffness = v);
        addDouble(advanced, eb, "Damping", "Base linear spring damping.", 1.0, 10000000.0, ServerConfig.INSTANCE.damping, v -> ServerConfig.INSTANCE.damping = v);
        addDouble(advanced, eb, "Angular Damping", "Base angular (rotational) damping.", 1.0, 10000000.0, ServerConfig.INSTANCE.angularDamping, v -> ServerConfig.INSTANCE.angularDamping = v);
        addDouble(advanced, eb, "Creative Strength Multiplier", "Multiplies stiffness, damping and force in Creative Super Strength mode.", 1.0, 100000.0, ServerConfig.INSTANCE.creativeStrengthMultiplier, v -> ServerConfig.INSTANCE.creativeStrengthMultiplier = v);
        addDouble(advanced, eb, "Speed Stiffness Factor", "Factor scaling spring stiffness with player speed.", 0.0, 100000.0, ServerConfig.INSTANCE.speedStiffnessMultiplierFactor, v -> ServerConfig.INSTANCE.speedStiffnessMultiplierFactor = v);
        addDouble(advanced, eb, "Max Speed Stiffness Mult", "Cap on the speed-based stiffness multiplier.", 1.0, 10000.0, ServerConfig.INSTANCE.maxSpeedStiffnessMultiplier, v -> ServerConfig.INSTANCE.maxSpeedStiffnessMultiplier = v);
        addDouble(advanced, eb, "Base Angular Force Factor", "Fraction of maxForce used as minimum angular force.", 0.0, 1.0, ServerConfig.INSTANCE.baseAngularForceFactor, v -> ServerConfig.INSTANCE.baseAngularForceFactor = v);
        addDouble(advanced, eb, "Stable Angular Force: Mass Base", "Constant term in the stable angular force formula.", 0.0, 1000000.0, ServerConfig.INSTANCE.stableAngularForceMassBase, v -> ServerConfig.INSTANCE.stableAngularForceMassBase = v);
        addDouble(advanced, eb, "Stable Angular Force: Mass Factor", "Mass coefficient in the stable angular force formula.", 0.0, 100000.0, ServerConfig.INSTANCE.stableAngularForceMassFactor, v -> ServerConfig.INSTANCE.stableAngularForceMassFactor = v);
        addDouble(advanced, eb, "Rotating Angular Stiffness: Base", "Base angular stiffness multiplier while actively rotating.", 0.0, 1000.0, ServerConfig.INSTANCE.rotatingAngularStiffnessBase, v -> ServerConfig.INSTANCE.rotatingAngularStiffnessBase = v);
        addDouble(advanced, eb, "Rotating Angular Stiffness: Range", "Range component of the rotation stiffness multiplier.", 0.0, 5000.0, ServerConfig.INSTANCE.rotatingAngularStiffnessRange, v -> ServerConfig.INSTANCE.rotatingAngularStiffnessRange = v);
        addDouble(advanced, eb, "Sway Angular Stiffness: Base", "Base angular stiffness multiplier when idle (sway mode).", 0.0, 1000.0, ServerConfig.INSTANCE.swayAngularStiffnessBase, v -> ServerConfig.INSTANCE.swayAngularStiffnessBase = v);
        addDouble(advanced, eb, "Sway Angular Stiffness: Range", "Range component of the idle sway stiffness multiplier.", 0.0, 5000.0, ServerConfig.INSTANCE.swayAngularStiffnessRange, v -> ServerConfig.INSTANCE.swayAngularStiffnessRange = v);
        addDouble(advanced, eb, "Stabilization Exponent", "Exponent applied to stabilization factors for non-linear rigidity curves.", 0.1, 10.0, ServerConfig.INSTANCE.stabilizationExponent, v -> ServerConfig.INSTANCE.stabilizationExponent = v);
        addDouble(advanced, eb, "Creative Max Motor Force", "Maximum numeric force limit for physics motors in Creative mode to prevent engine overflow.", 1.0, 1e15, ServerConfig.INSTANCE.creativeMaxMotorForce, v -> ServerConfig.INSTANCE.creativeMaxMotorForce = v);
        server.addEntry(advanced.build());

        SubCategoryBuilder movementPenalty = eb.startSubCategory(Component.literal("Movement Speed Penalty"));
        addDouble(movementPenalty, eb, "Base Movement Penalty", "Base movement speed reduction when holding any object.", 0.0, 1.0, ServerConfig.INSTANCE.baseMovementPenalty, v -> ServerConfig.INSTANCE.baseMovementPenalty = v);
        addDouble(movementPenalty, eb, "Weight Penalty Multiplier", "How much object weight increases movement penalty.", 0.0, 5.0, ServerConfig.INSTANCE.weightPenaltyMultiplier, v -> ServerConfig.INSTANCE.weightPenaltyMultiplier = v);
        addDouble(movementPenalty, eb, "Tension Penalty Multiplier", "How much pulling against tension increases movement penalty.", 0.0, 5.0, ServerConfig.INSTANCE.tensionPenaltyMultiplier, v -> ServerConfig.INSTANCE.tensionPenaltyMultiplier = v);
        addDouble(movementPenalty, eb, "Kinetic Penalty Multiplier", "How much block movement (falling/dragging) increases movement penalty.", 0.0, 5.0, ServerConfig.INSTANCE.kineticPenaltyMultiplier, v -> ServerConfig.INSTANCE.kineticPenaltyMultiplier = v);
        addDouble(movementPenalty, eb, "Min Speed While Grabbing", "Minimum movement speed allowed while grabbing (prevents complete freeze).", 0.0, 1.0, ServerConfig.INSTANCE.minSpeedWhileGrabbing, v -> ServerConfig.INSTANCE.minSpeedWhileGrabbing = v);
        addDouble(movementPenalty, eb, "Tension Penalty Start Offset", "Tension distance before movement penalty starts applying.", 0.0, 100.0, ServerConfig.INSTANCE.tensionPenaltyStartOffset, v -> ServerConfig.INSTANCE.tensionPenaltyStartOffset = v);
        addDouble(movementPenalty, eb, "Tension Penalty Max Distance", "Tension distance at which movement penalty reaches its maximum.", 0.1, 1000.0, ServerConfig.INSTANCE.tensionPenaltyMaxDistance, v -> ServerConfig.INSTANCE.tensionPenaltyMaxDistance = v);
        addDouble(movementPenalty, eb, "Kinetic Penalty Reference Speed", "Block speed at which kinetic movement penalty reaches its maximum.", 0.1, 100.0, ServerConfig.INSTANCE.kineticPenaltyReferenceSpeed, v -> ServerConfig.INSTANCE.kineticPenaltyReferenceSpeed = v);
        server.addEntry(movementPenalty.build());

        SubCategoryBuilder leadPrediction = eb.startSubCategory(Component.literal("Lead Prediction"));
        addDouble(leadPrediction, eb, "Lead Velocity Threshold", "Player speed threshold to activate target prediction.", 0.0, 100.0, ServerConfig.INSTANCE.leadVelocityThreshold, v -> ServerConfig.INSTANCE.leadVelocityThreshold = v);
        addDouble(leadPrediction, eb, "Lead Prediction Factor", "Multiplier for predicting the anchor target ahead of the player's movement.", 0.0, 100.0, ServerConfig.INSTANCE.leadPredictionFactor, v -> ServerConfig.INSTANCE.leadPredictionFactor = v);
        addDouble(leadPrediction, eb, "Lead Downward Clamp", "Clamps downward prediction to prevent the object from being dragged into the floor.", -100.0, 0.0, ServerConfig.INSTANCE.leadDownwardClamp, v -> ServerConfig.INSTANCE.leadDownwardClamp = v);
        server.addEntry(leadPrediction.build());
    }

    private static void buildClientCategories(ConfigCategory client, ConfigEntryBuilder eb) {
        SubCategoryBuilder rotInput = eb.startSubCategory(Component.literal("Rotation Input"));
        addDouble(rotInput, eb, "Vertical Sensitivity", "Mouse sensitivity multiplier for vertical rotation.", 0.1, 100.0, ClientConfig.INSTANCE.verticalRotationSensitivity, v -> ClientConfig.INSTANCE.verticalRotationSensitivity = v);
        addDouble(rotInput, eb, "Horizontal Sensitivity", "Mouse sensitivity multiplier for horizontal rotation.", 0.1, 100.0, ClientConfig.INSTANCE.horizontalRotationSensitivity, v -> ClientConfig.INSTANCE.horizontalRotationSensitivity = v);
        addBoolean(rotInput, eb, "Invert Vertical", "Inverts the vertical rotation axis.", ClientConfig.INSTANCE.invertVerticalRotation, v -> ClientConfig.INSTANCE.invertVerticalRotation = v);
        addBoolean(rotInput, eb, "Invert Horizontal", "Inverts the horizontal rotation axis.", ClientConfig.INSTANCE.invertHorizontalRotation, v -> ClientConfig.INSTANCE.invertHorizontalRotation = v);
        addBoolean(rotInput, eb, "Rotate Around Center", "Default pivot mode (Center of Mass vs Grab Point).", ClientConfig.INSTANCE.rotateAroundCenter, v -> ClientConfig.INSTANCE.rotateAroundCenter = v);
        addBoolean(rotInput, eb, "Prevent Movement While Rotating", "Freezes player movement inputs while holding the rotation key.", ClientConfig.INSTANCE.preventMovementWhileRotating, v -> ClientConfig.INSTANCE.preventMovementWhileRotating = v);
        client.addEntry(rotInput.build());

        SubCategoryBuilder armRendering = eb.startSubCategory(Component.literal("Arm & HUD Rendering"));
        addDouble(armRendering, eb, "Arm Transition Speed", "Per-tick speed of the arm hide/show transition.", 0.01, 10.0, ClientConfig.INSTANCE.armTransitionSpeed, v -> ClientConfig.INSTANCE.armTransitionSpeed = v);
        addDouble(armRendering, eb, "Arm Grab Lower Offset (m)", "How far the vanilla arm is pushed down during partial transitions.", 0.0, 100.0, ClientConfig.INSTANCE.armGrabLowerOffset, v -> ClientConfig.INSTANCE.armGrabLowerOffset = v);
        addDouble(armRendering, eb, "Assembly Shake Multiplier", "Camera shake scale while charging a block detach.", 0.0, 100.0, ClientConfig.INSTANCE.assemblyShakeMultiplier, v -> ClientConfig.INSTANCE.assemblyShakeMultiplier = v);
        addDouble(armRendering, eb, "Shake Frequency X", "Oscillation frequency for the camera shake on the X axis.", 0.1, 1000.0, ClientConfig.INSTANCE.shakeFrequencyX, v -> ClientConfig.INSTANCE.shakeFrequencyX = v);
        addDouble(armRendering, eb, "Shake Frequency Y", "Oscillation frequency for the camera shake on the Y axis.", 0.1, 1000.0, ClientConfig.INSTANCE.shakeFrequencyY, v -> ClientConfig.INSTANCE.shakeFrequencyY = v);
        addDouble(armRendering, eb, "Shake Frequency Z", "Oscillation frequency for the camera shake on the Z axis.", 0.1, 1000.0, ClientConfig.INSTANCE.shakeFrequencyZ, v -> ClientConfig.INSTANCE.shakeFrequencyZ = v);
        addDouble(armRendering, eb, "Arm Ease Full Threshold", "Ease progress above which the vanilla arm is fully hidden.", 0.5, 1.0, ClientConfig.INSTANCE.armEaseFullThreshold, v -> ClientConfig.INSTANCE.armEaseFullThreshold = v);
        addDouble(armRendering, eb, "Visual Shake Threshold", "Minimum charge progress before the shake effect appears.", 0.0, 1.0, ClientConfig.INSTANCE.visualShakeThreshold, v -> ClientConfig.INSTANCE.visualShakeThreshold = v);
        addBoolean(armRendering, eb, "Hide Hands While Grabbing", "Completely hide player arms while grabbing an object.", ClientConfig.INSTANCE.hideHandsWhileGrabbing, v -> ClientConfig.INSTANCE.hideHandsWhileGrabbing = v);
        addDouble(armRendering, eb, "Grab Arm Offset X", "Horizontal positioning of the grab arms.", -100.0, 100.0, ClientConfig.INSTANCE.grabArmOffsetX, v -> ClientConfig.INSTANCE.grabArmOffsetX = v);
        addDouble(armRendering, eb, "Grab Arm Offset Y", "Vertical positioning of the grab arms.", -100.0, 100.0, ClientConfig.INSTANCE.grabArmOffsetY, v -> ClientConfig.INSTANCE.grabArmOffsetY = v);
        addDouble(armRendering, eb, "Grab Arm Offset Z", "Depth positioning of the grab arms.", -100.0, 100.0, ClientConfig.INSTANCE.grabArmOffsetZ, v -> ClientConfig.INSTANCE.grabArmOffsetZ = v);
        client.addEntry(armRendering.build());

        SubCategoryBuilder interaction = eb.startSubCategory(Component.literal("Interaction"));
        addBoolean(interaction, eb, "Prevent Assembly When Mining", "Blocks pulling blocks if you are already mining them.", ClientConfig.INSTANCE.preventAssemblyWhenMining, v -> ClientConfig.INSTANCE.preventAssemblyWhenMining = v);
        addDouble(interaction, eb, "Assembly Mining Threshold", "Mining progress above which assembly is blocked.", 0.0, 1.0, ClientConfig.INSTANCE.barehandedAssemblyMiningThreshold, v -> ClientConfig.INSTANCE.barehandedAssemblyMiningThreshold = v);
        addInt(interaction, eb, "Regrab Debounce Ticks", "Ticks both keys must be released before allowing a new grab (prevents accidental regrab).", 0, 20, ClientConfig.INSTANCE.regrabDebounceTicks, v -> ClientConfig.INSTANCE.regrabDebounceTicks = v);
        client.addEntry(interaction.build());
    }

    private static void addDouble(SubCategoryBuilder sub, ConfigEntryBuilder eb, String title, String tooltip, double min, double max, double current, java.util.function.Consumer<Double> save) {
        var b = eb.startDoubleField(Component.literal(title), current).setDefaultValue(current).setMin(min).setMax(max).setSaveConsumer(save);
        if (tooltip != null && !tooltip.isEmpty()) b.setTooltip(Component.literal(tooltip));
        sub.add(b.build());
    }

    private static void addInt(SubCategoryBuilder sub, ConfigEntryBuilder eb, String title, String tooltip, int min, int max, int current, java.util.function.Consumer<Integer> save) {
        var b = eb.startIntField(Component.literal(title), current).setDefaultValue(current).setMin(min).setMax(max).setSaveConsumer(save);
        if (tooltip != null && !tooltip.isEmpty()) b.setTooltip(Component.literal(tooltip));
        sub.add(b.build());
    }

    private static void addBoolean(SubCategoryBuilder sub, ConfigEntryBuilder eb, String title, String tooltip, boolean current, java.util.function.Consumer<Boolean> save) {
        var b = eb.startBooleanToggle(Component.literal(title), current).setDefaultValue(current).setSaveConsumer(save);
        if (tooltip != null && !tooltip.isEmpty()) b.setTooltip(Component.literal(tooltip));
        sub.add(b.build());
    }
}