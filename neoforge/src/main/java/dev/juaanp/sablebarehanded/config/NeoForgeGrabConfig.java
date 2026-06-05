package dev.juaanp.sablebarehanded.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NeoForgeGrabConfig {
    public static class Common {
        public final ModConfigSpec.DoubleValue stiffness;
        public final ModConfigSpec.DoubleValue damping;
        public final ModConfigSpec.DoubleValue angularDamping;
        public final ModConfigSpec.DoubleValue maxForce;
        public final ModConfigSpec.DoubleValue minDistance;

        public final ModConfigSpec.BooleanValue enableRotation;
        public final ModConfigSpec.DoubleValue grabStabilization;
        public final ModConfigSpec.DoubleValue rotationStabilization;
        public final ModConfigSpec.BooleanValue preventFastRotations;
        public final ModConfigSpec.BooleanValue creativeSuperStrength;
        public final ModConfigSpec.DoubleValue strength1Multiplier;
        public final ModConfigSpec.DoubleValue strength2Multiplier;

        public final ModConfigSpec.DoubleValue rotationMassDampingFactor;
        public final ModConfigSpec.DoubleValue tensionSuspendThreshold;
        public final ModConfigSpec.DoubleValue tensionBreakThreshold;
        public final ModConfigSpec.DoubleValue maxPlayerVelocityYUp;
        public final ModConfigSpec.DoubleValue maxPlayerVelocityYDown;
        public final ModConfigSpec.DoubleValue maxPlayerVelocityXZ;
        public final ModConfigSpec.DoubleValue maxRotationSpeed;

        public final ModConfigSpec.BooleanValue ignoreCollisionsGrabEverything;
        public final ModConfigSpec.BooleanValue ignoreCollisionsGrabEntities;
        public final ModConfigSpec.BooleanValue ignoreCollisionsGrabOtherPlayers;
        public final ModConfigSpec.BooleanValue ignoreCollisionsGrabSelf;

        public final ModConfigSpec.BooleanValue ignoreCollisionsRotationEverything;
        public final ModConfigSpec.BooleanValue ignoreCollisionsRotationEntities;
        public final ModConfigSpec.BooleanValue ignoreCollisionsRotationOtherPlayers;
        public final ModConfigSpec.BooleanValue ignoreCollisionsRotationSelf;

        public final ModConfigSpec.BooleanValue enableBarehandedAssembly;
        public final ModConfigSpec.DoubleValue barehandedAssemblySpeedMultiplier;
        public final ModConfigSpec.DoubleValue barehandedAssemblyMaxDistance;

        public Common(ModConfigSpec.Builder builder) {

            builder.translation("sable-barehanded.config.physics.mechanics").push("mechanics");

            maxForce = builder.translation("sable-barehanded.config.physics.max_force")
                    .defineInRange("maxForce", 120.0, 1.0, 10000000.0);

            minDistance = builder.translation("sable-barehanded.config.physics.min_distance")
                    .defineInRange("minDistance", 2.0, 0.1, 1000.0);

            grabStabilization = builder.translation("sable-barehanded.config.physics.grab_stabilization")
                    .defineInRange("grabStabilization", 0.01, 0.0, 1.0);

            rotationStabilization = builder.translation("sable-barehanded.config.physics.rotation_stabilization")
                    .defineInRange("rotationStabilization", 0.5, 0.0, 1.0);

            enableRotation = builder.translation("sable-barehanded.config.physics.enable_rotation")
                    .define("enableRotation", true);

            maxRotationSpeed = builder.translation("sable-barehanded.config.physics.max_rotation_speed")
                    .defineInRange("maxRotationSpeed", 0.2, 0.0, 1.0);

            preventFastRotations = builder.translation("sable-barehanded.config.physics.prevent_fast_rotations")
                    .define("preventFastRotations", true);

            creativeSuperStrength = builder.translation("sable-barehanded.config.physics.creative_super_strength")
                    .define("creativeSuperStrength", true);

            strength1Multiplier = builder.translation("sable-barehanded.config.physics.strength_1_multiplier").defineInRange("strength1Multiplier", 2.0, 1.0, 100.0);
            strength2Multiplier = builder.translation("sable-barehanded.config.physics.strength_2_multiplier").defineInRange("strength2Multiplier", 4.0, 1.0, 100.0);

            enableBarehandedAssembly = builder.translation("sable-barehanded.config.physics.enable_barehanded_assembly")
                    .define("enableBarehandedAssembly", true);

            barehandedAssemblySpeedMultiplier = builder.translation("sable-barehanded.config.physics.barehanded_assembly_speed_multiplier")
                    .defineInRange("barehandedAssemblySpeedMultiplier", 1.0, 0.1, 10.0);

            barehandedAssemblyMaxDistance = builder.translation("sable-barehanded.config.physics.barehanded_assembly_max_distance")
                    .defineInRange("barehandedAssemblyMaxDistance", 2.5, 1.0, 10.0);

            builder.pop();

            builder.translation("sable-barehanded.config.physics.grab_collisions").push("grab_collisions");

            ignoreCollisionsGrabSelf = builder.translation("sable-barehanded.config.physics.ignore_collisions_self")
                    .define("ignoreCollisionsGrabSelf", false);

            ignoreCollisionsGrabOtherPlayers = builder.translation("sable-barehanded.config.physics.ignore_collisions_other_players")
                    .define("ignoreCollisionsGrabOtherPlayers", false);

            ignoreCollisionsGrabEntities = builder.translation("sable-barehanded.config.physics.ignore_collisions_entities")
                    .define("ignoreCollisionsGrabEntities", false);

            ignoreCollisionsGrabEverything = builder.translation("sable-barehanded.config.physics.ignore_collisions_everything")
                    .define("ignoreCollisionsGrabEverything", false);

            builder.pop();

            builder.translation("sable-barehanded.config.physics.rotation_collisions").push("rotation_collisions");

            ignoreCollisionsRotationSelf = builder.translation("sable-barehanded.config.physics.ignore_collisions_self")
                    .define("ignoreCollisionsRotationSelf", true);

            ignoreCollisionsRotationOtherPlayers = builder.translation("sable-barehanded.config.physics.ignore_collisions_other_players")
                    .define("ignoreCollisionsRotationOtherPlayers", true);

            ignoreCollisionsRotationEntities = builder.translation("sable-barehanded.config.physics.ignore_collisions_entities")
                    .define("ignoreCollisionsRotationEntities", true);

            ignoreCollisionsRotationEverything = builder.translation("sable-barehanded.config.physics.ignore_collisions_everything")
                    .define("ignoreCollisionsRotationEverything", false);

            builder.pop();

            builder.translation("sable-barehanded.config.physics.core_physics").push("core_physics");

            stiffness = builder.translation("sable-barehanded.config.physics.stiffness")
                    .defineInRange("stiffness", 1000.0, 1.0, 100000.0);

            damping = builder.translation("sable-barehanded.config.physics.damping")
                    .defineInRange("damping", 125.0, 1.0, 10000.0);

            angularDamping = builder.translation("sable-barehanded.config.physics.angular_damping")
                    .defineInRange("angularDamping", 850.0, 1.0, 10000.0);

            builder.pop();

            builder.translation("sable-barehanded.config.physics.advanced_physics").push("advanced_physics");

            rotationMassDampingFactor = builder.translation("sable-barehanded.config.physics.rotation_mass_damping")
                    .defineInRange("rotationMassDamping", 0.02, 0.0, 1.0);

            tensionSuspendThreshold = builder.translation("sable-barehanded.config.physics.tension_suspend_threshold")
                    .defineInRange("tensionSuspendThreshold", 3.5, 0.0, 50.0);

            tensionBreakThreshold = builder.translation("sable-barehanded.config.physics.tension_break_threshold")
                    .defineInRange("tensionBreakThreshold", 9.0, 0.0, 100.0);

            maxPlayerVelocityYUp = builder.translation("sable-barehanded.config.physics.max_player_velocity_y_up")
                    .defineInRange("maxPlayerVelocityYUp", 2.5, 0.0, 10.0);

            maxPlayerVelocityYDown = builder.translation("sable-barehanded.config.physics.max_player_velocity_y_down")
                    .defineInRange("maxPlayerVelocityYDown", -4.0, -20.0, 0.0);

            maxPlayerVelocityXZ = builder.translation("sable-barehanded.config.physics.max_player_velocity_xz")
                    .defineInRange("maxPlayerVelocityXZ", 2.5, 0.0, 10.0);

            builder.pop();
        }
    }

    public static class Client {
        public final ModConfigSpec.DoubleValue verticalRotationSensitivity;
        public final ModConfigSpec.DoubleValue horizontalRotationSensitivity;
        public final ModConfigSpec.BooleanValue invertVerticalRotation;
        public final ModConfigSpec.BooleanValue invertHorizontalRotation;
        public final ModConfigSpec.BooleanValue rotateAroundCenter;
        public final ModConfigSpec.BooleanValue preventMovementWhileRotating;
        public final ModConfigSpec.BooleanValue preventAssemblyWhenMining;
        public final ModConfigSpec.DoubleValue barehandedAssemblyMiningThreshold;

        public Client(ModConfigSpec.Builder builder) {
            verticalRotationSensitivity = builder.translation("sable-barehanded.config.client.vertical_rotation_sensitivity")
                    .defineInRange("verticalRotationSensitivity", 0.5, 0.1, 1.0);

            horizontalRotationSensitivity = builder.translation("sable-barehanded.config.client.horizontal_rotation_sensitivity")
                    .defineInRange("horizontalRotationSensitivity", 0.5, 0.1, 1.0);

            invertVerticalRotation = builder.translation("sable-barehanded.config.client.invert_vertical_rotation")
                    .define("invertVerticalRotation", false);

            invertHorizontalRotation = builder.translation("sable-barehanded.config.client.invert_horizontal_rotation")
                    .define("invertHorizontalRotation", false);

            rotateAroundCenter = builder.translation("sable-barehanded.config.client.rotate_around_center")
                    .define("rotateAroundCenter", false);

            preventMovementWhileRotating = builder.translation("sable-barehanded.config.client.prevent_movement_while_rotating")
                    .define("preventMovementWhileRotating", true);

            preventAssemblyWhenMining = builder.translation("sable-barehanded.config.client.prevent_assembly_when_mining")
                    .define("preventAssemblyWhenMining", true);

            barehandedAssemblyMiningThreshold = builder.translation("sable-barehanded.config.client.barehanded_assembly_mining_threshold")
                    .defineInRange("barehandedAssemblyMiningThreshold", 0.05, 0.0, 1.0);
        }
    }

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();

        Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
    }

    public static double stiffness() { return COMMON.stiffness.get(); }
    public static double damping() { return COMMON.damping.get(); }
    public static double angularDamping() { return COMMON.angularDamping.get(); }
    public static double maxForce() { return COMMON.maxForce.get(); }
    public static double minDistance() { return COMMON.minDistance.get(); }

    public static boolean enableRotation() { return COMMON.enableRotation.get(); }
    public static double grabStabilization() { return COMMON.grabStabilization.get(); }
    public static double rotationStabilization() { return COMMON.rotationStabilization.get(); }
    public static boolean preventFastRotations() { return COMMON.preventFastRotations.get(); }
    public static boolean creativeSuperStrength() { return COMMON.creativeSuperStrength.get(); }
    public static double strength1Multiplier() { return COMMON.strength1Multiplier.get(); }
    public static double strength2Multiplier() { return COMMON.strength2Multiplier.get(); }

    public static double rotationMassDampingFactor() { return COMMON.rotationMassDampingFactor.get(); }
    public static double tensionSuspendThreshold() { return COMMON.tensionSuspendThreshold.get(); }
    public static double tensionBreakThreshold() { return COMMON.tensionBreakThreshold.get(); }
    public static double maxPlayerVelocityYUp() { return COMMON.maxPlayerVelocityYUp.get(); }
    public static double maxPlayerVelocityYDown() { return COMMON.maxPlayerVelocityYDown.get(); }
    public static double maxPlayerVelocityXZ() { return COMMON.maxPlayerVelocityXZ.get(); }
    public static double maxRotationSpeed() { return COMMON.maxRotationSpeed.get(); }

    public static boolean ignoreCollisionsGrabEverything() { return COMMON.ignoreCollisionsGrabEverything.get(); }
    public static boolean ignoreCollisionsGrabEntities() { return COMMON.ignoreCollisionsGrabEntities.get(); }
    public static boolean ignoreCollisionsGrabOtherPlayers() { return COMMON.ignoreCollisionsGrabOtherPlayers.get(); }
    public static boolean ignoreCollisionsGrabSelf() { return COMMON.ignoreCollisionsGrabSelf.get(); }

    public static boolean ignoreCollisionsRotationSelf() { return COMMON.ignoreCollisionsRotationSelf.get(); }
    public static boolean ignoreCollisionsRotationEntities() { return COMMON.ignoreCollisionsRotationEntities.get(); }
    public static boolean ignoreCollisionsRotationOtherPlayers() { return COMMON.ignoreCollisionsRotationOtherPlayers.get(); }
    public static boolean ignoreCollisionsRotationEverything() { return COMMON.ignoreCollisionsRotationEverything.get(); }

    public static double verticalRotationSensitivity() { return CLIENT.verticalRotationSensitivity.get(); }
    public static double horizontalRotationSensitivity() { return CLIENT.horizontalRotationSensitivity.get(); }
    public static boolean invertVerticalRotation() { return CLIENT.invertVerticalRotation.get(); }
    public static boolean invertHorizontalRotation() { return CLIENT.invertHorizontalRotation.get(); }
    public static boolean rotateAroundCenter() { return CLIENT.rotateAroundCenter.get(); }
    public static boolean preventMovementWhileRotating() { return CLIENT.preventMovementWhileRotating.get(); }
    public static boolean preventAssemblyWhenMining() { return CLIENT.preventAssemblyWhenMining.get(); }
    public static double barehandedAssemblyMiningThreshold() { return CLIENT.barehandedAssemblyMiningThreshold.get(); }

    public static boolean enableBarehandedAssembly() { return COMMON.enableBarehandedAssembly.get(); }
    public static double barehandedAssemblySpeedMultiplier() { return COMMON.barehandedAssemblySpeedMultiplier.get(); }
    public static double barehandedAssemblyMaxDistance() { return COMMON.barehandedAssemblyMaxDistance.get(); }
}