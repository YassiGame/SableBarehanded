package dev.juaanp.sablebarehanded.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.juaanp.sablebarehanded.Constants;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

public class CommonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = Paths.get("config", Constants.MOD_ID + ".json").toFile();

    public record DoubleSpec(double def, double min, double max, String tooltip) {
        public DoubleSpec(double def, double min, double max) { this(def, min, max, null); }
    }

    public record IntSpec(int def, int min, int max, String tooltip) {
        public IntSpec(int def, int min, int max) { this(def, min, max, null); }
    }

    public record BooleanSpec(boolean def, String tooltip) {
        public BooleanSpec(boolean def) { this(def, null); }
    }

    public static class Specs {
        // Core
        public static final DoubleSpec MAX_FORCE = new DoubleSpec(120.0, 1.0, 1000000.0, "Maximum spring force applied to move a grabbed object.");
        public static final DoubleSpec MIN_DISTANCE = new DoubleSpec(1.5, 0.1, 1024.0, "Minimum hold distance from the player's eye to the anchor.");
        public static final DoubleSpec GRAB_REACH_BONUS = new DoubleSpec(0.0, 0.0, 1024.0, "Extra reach added on top of the block-interaction-range attribute.");
        public static final DoubleSpec GRAB_STABILIZATION = new DoubleSpec(0.01, 0.0, 1.0, "Positional stabilization factor (0 = loose, 1 = rigid).");
        public static final BooleanSpec CREATIVE_SUPER_STRENGTH = new BooleanSpec(true, "Creative players ignore weight limits and get boosted physics.");
        public static final DoubleSpec STRENGTH_1_MULTIPLIER = new DoubleSpec(2.0, 1.0, 10000.0);
        public static final DoubleSpec STRENGTH_2_MULTIPLIER = new DoubleSpec(4.0, 1.0, 10000.0);

        // Rotation
        public static final BooleanSpec ENABLE_ROTATION = new BooleanSpec(true);
        public static final DoubleSpec ROTATION_STABILIZATION = new DoubleSpec(0.3, 0.0, 1.0);
        public static final DoubleSpec MAX_ROTATION_SPEED = new DoubleSpec(0.2, 0.0, Math.PI);
        public static final BooleanSpec PREVENT_FAST_ROTATIONS = new BooleanSpec(true);
        public static final DoubleSpec ROTATION_MASS_DAMPING_FACTOR = new DoubleSpec(0.02, 0.0, 100.0, "How much object mass slows down rotation.");
        public static final IntSpec ROTATION_TICKS_WINDOW = new IntSpec(5, 1, 1200, "Ticks the rotation motor stays active after the last mouse input.");
        public static final DoubleSpec ROTATION_REBUILD_THRESHOLD = new DoubleSpec(0.25, 0.01, Math.PI, "Angle (radians) that triggers a constraint pivot rebuild.");

        // Assembly
        public static final BooleanSpec ENABLE_BAREHANDED_ASSEMBLY = new BooleanSpec(true);
        public static final DoubleSpec BAREHANDED_ASSEMBLY_SPEED_MULTIPLIER = new DoubleSpec(1.0, 0.1, 1000.0);
        public static final DoubleSpec BAREHANDED_ASSEMBLY_MAX_DISTANCE = new DoubleSpec(3.0, 1.0, 1024.0);
        public static final DoubleSpec ASSEMBLY_SERVER_DISTANCE_TOLERANCE = new DoubleSpec(1.0, 0.0, 1024.0, "Server-side grace distance added when validating assembly requests.");
        public static final DoubleSpec ASSEMBLY_CLIENT_DISTANCE_TOLERANCE = new DoubleSpec(1.5, 0.0, 1024.0, "Client-side grace distance before cancelling the charge.");
        public static final IntSpec FAST_LIFT_ASSEMBLY_TICKS = new IntSpec(2, 1, 12000, "Charge ticks for blocks with a BlockEntity but non-full collision (chests, barrels...).");
        public static final DoubleSpec PULL_THRESHOLD = new DoubleSpec(0.05, 0.0, 100.0);
        public static final DoubleSpec PULL_RESISTANCE_MULTIPLIER = new DoubleSpec(0.6, 0.0, 1000.0);
        public static final DoubleSpec ASSEMBLY_MOVEMENT_DAMPING = new DoubleSpec(0.5, 0.0, 1.0);

        // Collisions Grab
        public static final BooleanSpec IGNORE_COLLISIONS_GRAB_EVERYTHING = new BooleanSpec(false);
        public static final BooleanSpec IGNORE_COLLISIONS_GRAB_ENTITIES = new BooleanSpec(false);
        public static final BooleanSpec IGNORE_COLLISIONS_GRAB_OTHER_PLAYERS = new BooleanSpec(false);
        public static final BooleanSpec IGNORE_COLLISIONS_GRAB_SELF = new BooleanSpec(false);

        // Collisions Rotation
        public static final BooleanSpec IGNORE_COLLISIONS_ROTATION_EVERYTHING = new BooleanSpec(false);
        public static final BooleanSpec IGNORE_COLLISIONS_ROTATION_ENTITIES = new BooleanSpec(true);
        public static final BooleanSpec IGNORE_COLLISIONS_ROTATION_OTHER_PLAYERS = new BooleanSpec(true);
        public static final BooleanSpec IGNORE_COLLISIONS_ROTATION_SELF = new BooleanSpec(true);

        // Suspension
        public static final IntSpec STANDING_ON_GRAB_SUSPEND_TICKS = new IntSpec(15, 0, 12000, "Ticks physics stays suspended after stepping off a grabbed object.");
        public static final DoubleSpec GRAB_PROXIMITY_EYE_SUSPEND_DISTANCE = new DoubleSpec(1.0, 0.0, 1024.0, "Eye-to-block distance that suspends physics to prevent clipping.");
        public static final DoubleSpec GRAB_PROXIMITY_BODY_SUSPEND_DISTANCE = new DoubleSpec(1.5, 0.0, 1024.0, "Foot-to-block distance that suspends physics to prevent clipping.");
        public static final DoubleSpec TENSION_SUSPEND_THRESHOLD = new DoubleSpec(2.5, 0.0, 10000.0);
        public static final DoubleSpec TENSION_BREAK_THRESHOLD = new DoubleSpec(2.0, 0.0, 10000.0);
        public static final DoubleSpec CREATIVE_TENSION_SUSPEND_THRESHOLD = new DoubleSpec(64.0, 0.0, 10000.0, "Tension suspend threshold in Creative Super Strength mode.");
        public static final DoubleSpec CREATIVE_TENSION_BREAK_THRESHOLD = new DoubleSpec(64.0, 0.0, 10000.0, "Tension break threshold in Creative Super Strength mode.");

        // Velocity
        public static final DoubleSpec MAX_PLAYER_VELOCITY_Y_UP = new DoubleSpec(2.5, 0.0, 1000.0);
        public static final DoubleSpec MAX_PLAYER_VELOCITY_Y_DOWN = new DoubleSpec(-4.0, -1000.0, 0.0);
        public static final DoubleSpec MAX_PLAYER_VELOCITY_XZ = new DoubleSpec(2.5, 0.0, 1000.0);

        // Advanced
        public static final DoubleSpec STIFFNESS = new DoubleSpec(1000.0, 1.0, 10000000.0);
        public static final DoubleSpec DAMPING = new DoubleSpec(125.0, 1.0, 10000000.0);
        public static final DoubleSpec ANGULAR_DAMPING = new DoubleSpec(850.0, 1.0, 10000000.0);
        public static final DoubleSpec CREATIVE_STRENGTH_MULTIPLIER = new DoubleSpec(10.0, 1.0, 100000.0, "Multiplies stiffness, damping and force in Creative Super Strength mode.");
        public static final DoubleSpec SPEED_STIFFNESS_MULTIPLIER_FACTOR = new DoubleSpec(15.0, 0.0, 100000.0, "Factor scaling spring stiffness with player speed.");
        public static final DoubleSpec MAX_SPEED_STIFFNESS_MULTIPLIER = new DoubleSpec(8.0, 1.0, 10000.0, "Cap on the speed-based stiffness multiplier.");
        public static final DoubleSpec BASE_ANGULAR_FORCE_FACTOR = new DoubleSpec(0.15, 0.0, 1.0, "Fraction of maxForce used as minimum angular force.");
        public static final DoubleSpec STABLE_ANGULAR_FORCE_MASS_BASE = new DoubleSpec(10.0, 0.0, 1000000.0, "Constant term: stableForce = maxForce * (base + mass * factor).");
        public static final DoubleSpec STABLE_ANGULAR_FORCE_MASS_FACTOR = new DoubleSpec(0.5, 0.0, 100000.0, "Mass coefficient in the stable angular force formula.");
        public static final DoubleSpec ROTATING_ANGULAR_STIFFNESS_BASE = new DoubleSpec(1.5, 0.0, 1000.0, "Base angular stiffness multiplier while actively rotating.");
        public static final DoubleSpec ROTATING_ANGULAR_STIFFNESS_RANGE = new DoubleSpec(4.5, 0.0, 5000.0, "Range component of the rotation stiffness multiplier.");
        public static final DoubleSpec SWAY_ANGULAR_STIFFNESS_BASE = new DoubleSpec(0.6, 0.0, 1000.0, "Base angular stiffness multiplier when idle (sway mode).");
        public static final DoubleSpec SWAY_ANGULAR_STIFFNESS_RANGE = new DoubleSpec(5.4, 0.0, 5000.0, "Range component of the idle sway stiffness multiplier.");

        // Movement Speed Penalty
        public static final DoubleSpec BASE_MOVEMENT_PENALTY = new DoubleSpec(0.0, 0.0, 1.0, "Base movement speed reduction when holding any object (0 = no penalty, 1 = can't move).");
        public static final DoubleSpec WEIGHT_PENALTY_MULTIPLIER = new DoubleSpec(0.02, 0.0, 5.0, "How much object weight increases movement penalty.");
        public static final DoubleSpec TENSION_PENALTY_MULTIPLIER = new DoubleSpec(0.05, 0.0, 5.0, "How much pulling against tension increases movement penalty.");
        public static final DoubleSpec KINETIC_PENALTY_MULTIPLIER = new DoubleSpec(0.02, 0.0, 5.0, "How much block movement (falling/dragging) increases movement penalty.");
        public static final DoubleSpec MIN_SPEED_WHILE_GRABBING = new DoubleSpec(0.05, 0.0, 1.0, "Minimum movement speed allowed while grabbing (prevents complete freeze).");

        // Client Input
        public static final DoubleSpec VERTICAL_ROTATION_SENSITIVITY = new DoubleSpec(0.5, 0.1, 100.0);
        public static final DoubleSpec HORIZONTAL_ROTATION_SENSITIVITY = new DoubleSpec(0.5, 0.1, 100.0);
        public static final BooleanSpec INVERT_VERTICAL_ROTATION = new BooleanSpec(false);
        public static final BooleanSpec INVERT_HORIZONTAL_ROTATION = new BooleanSpec(false);
        public static final BooleanSpec ROTATE_AROUND_CENTER = new BooleanSpec(false);
        public static final BooleanSpec PREVENT_MOVEMENT_WHILE_ROTATING = new BooleanSpec(true);

        // Client Rendering
        public static final DoubleSpec ARM_TRANSITION_SPEED = new DoubleSpec(0.14, 0.01, 10.0, "Per-tick speed of the arm hide/show transition.");
        public static final DoubleSpec ARM_GRAB_LOWER_OFFSET = new DoubleSpec(1.5, 0.0, 100.0, "How far the vanilla arm is pushed down during partial transitions.");
        public static final DoubleSpec ASSEMBLY_SHAKE_MULTIPLIER = new DoubleSpec(0.04, 0.0, 100.0, "Camera shake scale while charging a block detach.");
        public static final DoubleSpec SHAKE_FREQUENCY_X = new DoubleSpec(3.0, 0.1, 1000.0);
        public static final DoubleSpec SHAKE_FREQUENCY_Y = new DoubleSpec(4.0, 0.1, 1000.0);
        public static final DoubleSpec SHAKE_FREQUENCY_Z = new DoubleSpec(5.0, 0.1, 1000.0);
        public static final DoubleSpec ARM_EASE_FULL_THRESHOLD = new DoubleSpec(0.99, 0.5, 1.0, "Ease progress above which the vanilla arm is fully hidden.");
        public static final DoubleSpec VISUAL_SHAKE_THRESHOLD = new DoubleSpec(0.3, 0.0, 1.0, "Minimum charge progress before the shake effect appears.");
        public static final BooleanSpec HIDE_HANDS_WHILE_GRABBING = new BooleanSpec(false, "Completely hide player arms while grabbing an object.");
        public static final DoubleSpec GRAB_ARM_OFFSET_X = new DoubleSpec(0.18, -100.0, 100.0, "Horizontal positioning of the grab arms.");
        public static final DoubleSpec GRAB_ARM_OFFSET_Y = new DoubleSpec(-0.6, -100.0, 100.0, "Vertical positioning of the grab arms.");
        public static final DoubleSpec GRAB_ARM_OFFSET_Z = new DoubleSpec(-0.2, -100.0, 100.0, "Depth positioning of the grab arms.");

        // Client Mining
        public static final BooleanSpec PREVENT_ASSEMBLY_WHEN_MINING = new BooleanSpec(true);
        public static final DoubleSpec BAREHANDED_ASSEMBLY_MINING_THRESHOLD = new DoubleSpec(0.08, 0.0, 1.0, "Mining progress above which assembly is blocked.");

        // Client Input - Regrab
        public static final IntSpec REGRAB_DEBOUNCE_TICKS = new IntSpec(2, 0, 20, "Ticks both keys must be released before allowing a new grab (prevents accidental regrab).");

        // Encumbrance & Tethering
        public static final BooleanSpec ENABLE_ENCUMBRANCE = new BooleanSpec(true, "Enables player movement and camera penalties based on grabbed object mass.");
        public static final DoubleSpec PHYSICS_GRAVITY = new DoubleSpec(9.81, 0.1, 1000.0, "Gravity constant (m/s^2) used to convert mass into resting force (Weight = Mass * Gravity).");
        public static final DoubleSpec MAX_MOVEMENT_PENALTY = new DoubleSpec(0.85, 0.0, 1.0, "Maximum speed reduction (0.85 = 85% slower) when holding heavy objects.");
        public static final DoubleSpec JUMP_PREVENTION_THRESHOLD = new DoubleSpec(0.70, 0.0, 1.0, "Encumbrance ratio (0.0 to 1.0) at which jumping is disabled.");
        public static final DoubleSpec MAX_CAMERA_PENALTY = new DoubleSpec(0.60, 0.0, 1.0, "Maximum camera sensitivity reduction when holding heavy objects or pulling against tension.");
        public static final BooleanSpec ENABLE_PHYSICAL_TETHER = new BooleanSpec(true, "Enables the physical pull-back when stretching arms beyond the limit.");
        public static final DoubleSpec ARM_STRETCH_TOLERANCE = new DoubleSpec(0.3, 0.0, 50.0, "Extra distance (m) arms can stretch before the tether pulls the player.");
        public static final DoubleSpec TETHER_STIFFNESS_BASE = new DoubleSpec(0.15, 0.0, 10.0, "Base pull strength of the tether when stretched.");
        public static final DoubleSpec TETHER_STIFFNESS_MULTIPLIER = new DoubleSpec(0.85, 0.0, 50.0, "Additional pull strength based on the object's encumbrance.");

        // Exhaustion (Hunger) - MUCHO MÁS FUERTE
        public static final BooleanSpec ENABLE_EXHAUSTION = new BooleanSpec(true, "Consumes player hunger based on physical effort when grabbing objects.");
        public static final DoubleSpec EXHAUSTION_IDLE_RATE = new DoubleSpec(0.015, 0.0, 100.0, "Exhaustion added per tick just by holding a heavy object (0 = disabled).");
        public static final DoubleSpec EXHAUSTION_MOVEMENT_RATE = new DoubleSpec(0.8, 0.0, 100.0, "Exhaustion multiplier based on player movement speed while holding.");
        public static final DoubleSpec EXHAUSTION_TENSION_RATE = new DoubleSpec(0.3, 0.0, 100.0, "Exhaustion multiplier when pulling against a stuck object (tension).");
        public static final DoubleSpec EXHAUSTION_FORCE_RATE = new DoubleSpec(0.6, 0.0, 100.0, "Exhaustion multiplier based on net physical force applied to the object.");
        public static final DoubleSpec EXHAUSTION_PASSIVE_THRESHOLD = new DoubleSpec(0.9, 0.0, 1.0, "Fraction of object weight considered passive (ignored) when the object is supported/still.");
    }

    public static class Common {
        public double maxForce = Specs.MAX_FORCE.def();
        public double minDistance = Specs.MIN_DISTANCE.def();
        public double grabReachBonus = Specs.GRAB_REACH_BONUS.def();
        public double grabStabilization = Specs.GRAB_STABILIZATION.def();
        public boolean creativeSuperStrength = Specs.CREATIVE_SUPER_STRENGTH.def();
        public double strength1Multiplier = Specs.STRENGTH_1_MULTIPLIER.def();
        public double strength2Multiplier = Specs.STRENGTH_2_MULTIPLIER.def();

        public boolean enableRotation = Specs.ENABLE_ROTATION.def();
        public double rotationStabilization = Specs.ROTATION_STABILIZATION.def();
        public double maxRotationSpeed = Specs.MAX_ROTATION_SPEED.def();
        public boolean preventFastRotations = Specs.PREVENT_FAST_ROTATIONS.def();
        public double rotationMassDampingFactor = Specs.ROTATION_MASS_DAMPING_FACTOR.def();
        public int rotationTicksWindow = Specs.ROTATION_TICKS_WINDOW.def();
        public double rotationRebuildThreshold = Specs.ROTATION_REBUILD_THRESHOLD.def();

        public boolean enableBarehandedAssembly = Specs.ENABLE_BAREHANDED_ASSEMBLY.def();
        public double barehandedAssemblySpeedMultiplier = Specs.BAREHANDED_ASSEMBLY_SPEED_MULTIPLIER.def();
        public double barehandedAssemblyMaxDistance = Specs.BAREHANDED_ASSEMBLY_MAX_DISTANCE.def();
        public double assemblyServerDistanceTolerance = Specs.ASSEMBLY_SERVER_DISTANCE_TOLERANCE.def();
        public double assemblyClientDistanceTolerance = Specs.ASSEMBLY_CLIENT_DISTANCE_TOLERANCE.def();
        public int fastLiftAssemblyTicks = Specs.FAST_LIFT_ASSEMBLY_TICKS.def();
        public double pullThreshold = Specs.PULL_THRESHOLD.def();
        public double pullResistanceMultiplier = Specs.PULL_RESISTANCE_MULTIPLIER.def();
        public double assemblyMovementDamping = Specs.ASSEMBLY_MOVEMENT_DAMPING.def();

        public boolean ignoreCollisionsGrabEverything = Specs.IGNORE_COLLISIONS_GRAB_EVERYTHING.def();
        public boolean ignoreCollisionsGrabEntities = Specs.IGNORE_COLLISIONS_GRAB_ENTITIES.def();
        public boolean ignoreCollisionsGrabOtherPlayers = Specs.IGNORE_COLLISIONS_GRAB_OTHER_PLAYERS.def();
        public boolean ignoreCollisionsGrabSelf = Specs.IGNORE_COLLISIONS_GRAB_SELF.def();

        public boolean ignoreCollisionsRotationEverything = Specs.IGNORE_COLLISIONS_ROTATION_EVERYTHING.def();
        public boolean ignoreCollisionsRotationEntities = Specs.IGNORE_COLLISIONS_ROTATION_ENTITIES.def();
        public boolean ignoreCollisionsRotationOtherPlayers = Specs.IGNORE_COLLISIONS_ROTATION_OTHER_PLAYERS.def();
        public boolean ignoreCollisionsRotationSelf = Specs.IGNORE_COLLISIONS_ROTATION_SELF.def();

        public int standingOnGrabSuspendTicks = Specs.STANDING_ON_GRAB_SUSPEND_TICKS.def();
        public double grabProximityEyeSuspendDistance = Specs.GRAB_PROXIMITY_EYE_SUSPEND_DISTANCE.def();
        public double grabProximityBodySuspendDistance = Specs.GRAB_PROXIMITY_BODY_SUSPEND_DISTANCE.def();
        public double tensionSuspendThreshold = Specs.TENSION_SUSPEND_THRESHOLD.def();
        public double tensionBreakThreshold = Specs.TENSION_BREAK_THRESHOLD.def();
        public double creativeTensionSuspendThreshold = Specs.CREATIVE_TENSION_SUSPEND_THRESHOLD.def();
        public double creativeTensionBreakThreshold = Specs.CREATIVE_TENSION_BREAK_THRESHOLD.def();

        public double maxPlayerVelocityYUp = Specs.MAX_PLAYER_VELOCITY_Y_UP.def();
        public double maxPlayerVelocityYDown = Specs.MAX_PLAYER_VELOCITY_Y_DOWN.def();
        public double maxPlayerVelocityXZ = Specs.MAX_PLAYER_VELOCITY_XZ.def();

        public double stiffness = Specs.STIFFNESS.def();
        public double damping = Specs.DAMPING.def();
        public double angularDamping = Specs.ANGULAR_DAMPING.def();
        public double creativeStrengthMultiplier = Specs.CREATIVE_STRENGTH_MULTIPLIER.def();
        public double speedStiffnessMultiplierFactor = Specs.SPEED_STIFFNESS_MULTIPLIER_FACTOR.def();
        public double maxSpeedStiffnessMultiplier = Specs.MAX_SPEED_STIFFNESS_MULTIPLIER.def();
        public double baseAngularForceFactor = Specs.BASE_ANGULAR_FORCE_FACTOR.def();
        public double stableAngularForceMassBase = Specs.STABLE_ANGULAR_FORCE_MASS_BASE.def();
        public double stableAngularForceMassFactor = Specs.STABLE_ANGULAR_FORCE_MASS_FACTOR.def();
        public double rotatingAngularStiffnessBase = Specs.ROTATING_ANGULAR_STIFFNESS_BASE.def();
        public double rotatingAngularStiffnessRange = Specs.ROTATING_ANGULAR_STIFFNESS_RANGE.def();
        public double swayAngularStiffnessBase = Specs.SWAY_ANGULAR_STIFFNESS_BASE.def();
        public double swayAngularStiffnessRange = Specs.SWAY_ANGULAR_STIFFNESS_RANGE.def();

        public double baseMovementPenalty = Specs.BASE_MOVEMENT_PENALTY.def();
        public double weightPenaltyMultiplier = Specs.WEIGHT_PENALTY_MULTIPLIER.def();
        public double tensionPenaltyMultiplier = Specs.TENSION_PENALTY_MULTIPLIER.def();
        public double kineticPenaltyMultiplier = Specs.KINETIC_PENALTY_MULTIPLIER.def();
        public double minSpeedWhileGrabbing = Specs.MIN_SPEED_WHILE_GRABBING.def();

        public boolean enableEncumbrance = Specs.ENABLE_ENCUMBRANCE.def();
        public double physicsGravity = Specs.PHYSICS_GRAVITY.def();
        public double maxMovementPenalty = Specs.MAX_MOVEMENT_PENALTY.def();
        public double jumpPreventionThreshold = Specs.JUMP_PREVENTION_THRESHOLD.def();
        public double maxCameraPenalty = Specs.MAX_CAMERA_PENALTY.def();
        public boolean enablePhysicalTether = Specs.ENABLE_PHYSICAL_TETHER.def();
        public double armStretchTolerance = Specs.ARM_STRETCH_TOLERANCE.def();
        public double tetherStiffnessBase = Specs.TETHER_STIFFNESS_BASE.def();
        public double tetherStiffnessMultiplier = Specs.TETHER_STIFFNESS_MULTIPLIER.def();

        public boolean enableExhaustion = Specs.ENABLE_EXHAUSTION.def();
        public double exhaustionIdleRate = Specs.EXHAUSTION_IDLE_RATE.def();
        public double exhaustionMovementRate = Specs.EXHAUSTION_MOVEMENT_RATE.def();
        public double exhaustionTensionRate = Specs.EXHAUSTION_TENSION_RATE.def();
        public double exhaustionForceRate = Specs.EXHAUSTION_FORCE_RATE.def();
        public double exhaustionPassiveThreshold = Specs.EXHAUSTION_PASSIVE_THRESHOLD.def();
    }

    public static class Client {
        public double verticalRotationSensitivity = Specs.VERTICAL_ROTATION_SENSITIVITY.def();
        public double horizontalRotationSensitivity = Specs.HORIZONTAL_ROTATION_SENSITIVITY.def();
        public boolean invertVerticalRotation = Specs.INVERT_VERTICAL_ROTATION.def();
        public boolean invertHorizontalRotation = Specs.INVERT_HORIZONTAL_ROTATION.def();
        public boolean rotateAroundCenter = Specs.ROTATE_AROUND_CENTER.def();
        public boolean preventMovementWhileRotating = Specs.PREVENT_MOVEMENT_WHILE_ROTATING.def();

        public double armTransitionSpeed = Specs.ARM_TRANSITION_SPEED.def();
        public double armGrabLowerOffset = Specs.ARM_GRAB_LOWER_OFFSET.def();
        public double assemblyShakeMultiplier = Specs.ASSEMBLY_SHAKE_MULTIPLIER.def();
        public double shakeFrequencyX = Specs.SHAKE_FREQUENCY_X.def();
        public double shakeFrequencyY = Specs.SHAKE_FREQUENCY_Y.def();
        public double shakeFrequencyZ = Specs.SHAKE_FREQUENCY_Z.def();
        public double armEaseFullThreshold = Specs.ARM_EASE_FULL_THRESHOLD.def();
        public double visualShakeThreshold = Specs.VISUAL_SHAKE_THRESHOLD.def();

        public boolean hideHandsWhileGrabbing = Specs.HIDE_HANDS_WHILE_GRABBING.def();
        public double grabArmOffsetX = Specs.GRAB_ARM_OFFSET_X.def();
        public double grabArmOffsetY = Specs.GRAB_ARM_OFFSET_Y.def();
        public double grabArmOffsetZ = Specs.GRAB_ARM_OFFSET_Z.def();

        public boolean preventAssemblyWhenMining = Specs.PREVENT_ASSEMBLY_WHEN_MINING.def();
        public double barehandedAssemblyMiningThreshold = Specs.BAREHANDED_ASSEMBLY_MINING_THRESHOLD.def();

        // Movido de Common a Client
        public int regrabDebounceTicks = Specs.REGRAB_DEBOUNCE_TICKS.def();
    }

    public static Common COMMON = new Common();
    public static Client CLIENT = new Client();

    private static class ConfigWrapper {
        public Common COMMON;
        public Client CLIENT;
        public ConfigWrapper(Common common, Client client) {
            this.COMMON = common;
            this.CLIENT = client;
        }
    }

    public static void load() {
        try {
            if (FILE.exists()) {
                try (FileReader reader = new FileReader(FILE)) {
                    ConfigWrapper loaded = GSON.fromJson(reader, ConfigWrapper.class);
                    if (loaded != null) {
                        COMMON = loaded.COMMON != null ? loaded.COMMON : new Common();
                        CLIENT = loaded.CLIENT != null ? loaded.CLIENT : new Client();
                    }
                }
            } else {
                save();
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to load common config", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(new ConfigWrapper(COMMON, CLIENT), writer);
        } catch (Exception e) {
            Constants.LOG.error("Failed to save common config", e);
        }
    }

    public static String getCommonJson() {
        return GSON.toJson(COMMON);
    }

    public static void loadCommonFromJson(String json) {
        Common parsed = GSON.fromJson(json, Common.class);
        if (parsed != null) {
            COMMON = parsed;
        }
    }
}