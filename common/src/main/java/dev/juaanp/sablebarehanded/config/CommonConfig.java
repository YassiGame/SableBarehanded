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

    public static class Common {
        public double maxForce                  = 120.0;
        public double minDistance               = 2.0;
        public double grabReachBonus            = 2.0;
        public double grabStabilization         = 0.01;
        public boolean creativeSuperStrength    = true;
        public double strength1Multiplier       = 2.0;
        public double strength2Multiplier       = 4.0;

        public boolean enableRotation           = true;
        public double rotationStabilization     = 0.5;
        public double maxRotationSpeed          = 0.2;
        public boolean preventFastRotations     = true;
        public double rotationMassDampingFactor = 0.02;
        public int rotationTicksWindow          = 5;
        public double rotationRebuildThreshold  = 0.25;

        public boolean enableBarehandedAssembly            = true;
        public double barehandedAssemblySpeedMultiplier    = 1.0;
        public double barehandedAssemblyMaxDistance        = 2.0;
        public double assemblyServerDistanceTolerance      = 1.0;
        public double assemblyClientDistanceTolerance      = 1.5;
        public int fastLiftAssemblyTicks                   = 2;
        public double pullThreshold                        = 0.05;
        public double pullResistanceMultiplier             = 0.6;
        public double assemblyMovementDamping              = 0.5;

        public boolean ignoreCollisionsGrabEverything      = false;
        public boolean ignoreCollisionsGrabEntities        = false;
        public boolean ignoreCollisionsGrabOtherPlayers    = false;
        public boolean ignoreCollisionsGrabSelf            = false;

        public boolean ignoreCollisionsRotationEverything  = false;
        public boolean ignoreCollisionsRotationEntities    = true;
        public boolean ignoreCollisionsRotationOtherPlayers= true;
        public boolean ignoreCollisionsRotationSelf        = true;

        public int standingOnGrabSuspendTicks           = 15;
        public double grabProximityEyeSuspendDistance   = 1.0;
        public double grabProximityBodySuspendDistance  = 1.5;
        public double tensionSuspendThreshold           = 3.5;
        public double tensionBreakThreshold             = 9.0;
        public double creativeTensionSuspendThreshold   = 64.0;
        public double creativeTensionBreakThreshold     = 64.0;

        public double maxPlayerVelocityYUp   = 2.5;
        public double maxPlayerVelocityYDown = -4.0;
        public double maxPlayerVelocityXZ    = 2.5;

        public double playerSpeedLeadThreshold    = 0.1;
        public double playerSpeedLeadMultiplier   = 2.0;
        public double playerSpeedLeadYDownCap     = -0.5;

        public double stiffness                      = 1000.0;
        public double damping                        = 125.0;
        public double angularDamping                 = 850.0;
        public double creativeStrengthMultiplier     = 10.0;
        public double speedStiffnessMultiplierFactor = 15.0;
        public double maxSpeedStiffnessMultiplier    = 8.0;
        public double baseAngularForceFactor         = 0.15;
        public double stableAngularForceMassBase     = 10.0;
        public double stableAngularForceMassFactor   = 0.5;
        public double rotatingAngularStiffnessBase   = 1.5;
        public double rotatingAngularStiffnessRange  = 4.5;
        public double swayAngularStiffnessBase       = 0.6;
        public double swayAngularStiffnessRange      = 5.4;
    }

    public static class Client {
        public double verticalRotationSensitivity   = 0.5;
        public double horizontalRotationSensitivity = 0.5;
        public boolean invertVerticalRotation       = false;
        public boolean invertHorizontalRotation     = false;
        public boolean rotateAroundCenter           = false;
        public boolean preventMovementWhileRotating = true;

        public double armTransitionSpeed      = 0.2;
        public double armGrabLowerOffset      = 1.5;
        public double assemblyShakeMultiplier = 0.04;
        public double shakeFrequencyX         = 3.0;
        public double shakeFrequencyY         = 4.0;
        public double shakeFrequencyZ         = 5.0;
        public double armEaseFullThreshold    = 0.99;
        public double visualShakeThreshold    = 0.3;

        public boolean preventAssemblyWhenMining          = true;
        public double barehandedAssemblyMiningThreshold   = 0.05;
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
            Constants.LOG.info("Sable Barehanded: Configuración sincronizada con el servidor.");
        }
    }
}