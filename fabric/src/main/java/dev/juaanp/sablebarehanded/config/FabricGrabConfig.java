package dev.juaanp.sablebarehanded.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.juaanp.sablebarehanded.Constants;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FabricGrabConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), Constants.MOD_ID + ".json");

    public static class Common {
        public double stiffness = 1000.0;
        public double damping = 125.0;
        public double angularDamping = 850.0;
        public double maxForce = 120.0;
        public double minDistance = 2.0;

        public boolean enableRotation = true;
        public double grabStabilization = 0.01;
        public double rotationStabilization = 0.5;
        public boolean preventFastRotations = true;
        public boolean creativeSuperStrength = true;

        public double strength1Multiplier = 2.0;
        public double strength2Multiplier = 4.0;

        public double rotationMassDampingFactor = 0.02;
        public double tensionSuspendThreshold = 3.5;
        public double tensionBreakThreshold = 9.0;
        public double maxPlayerVelocityYUp = 2.5;
        public double maxPlayerVelocityYDown = -4.0;
        public double maxPlayerVelocityXZ = 2.5;
        public double maxRotationSpeed = 0.2;

        public boolean ignoreCollisionsGrabEverything = false;
        public boolean ignoreCollisionsGrabEntities = false;
        public boolean ignoreCollisionsGrabOtherPlayers = false;
        public boolean ignoreCollisionsGrabSelf = false;

        public boolean ignoreCollisionsRotationEverything = false;
        public boolean ignoreCollisionsRotationEntities = true;
        public boolean ignoreCollisionsRotationOtherPlayers = true;
        public boolean ignoreCollisionsRotationSelf = true;

        public boolean enableBarehandedAssembly = true;
        public double barehandedAssemblySpeedMultiplier = 1.0;
        public double barehandedAssemblyMaxDistance = 2.0;
    }

    public static class Client {
        public double verticalRotationSensitivity = 0.5;
        public double horizontalRotationSensitivity = 0.5;
        public boolean invertVerticalRotation = false;
        public boolean invertHorizontalRotation = false;
        public boolean rotateAroundCenter = false;
        public boolean preventMovementWhileRotating = true;
        public boolean preventAssemblyWhenMining = true;
        public double barehandedAssemblyMiningThreshold = 0.05;
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
            Constants.LOG.error("Failed to load Fabric config", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(new ConfigWrapper(COMMON, CLIENT), writer);
        } catch (Exception e) {
            Constants.LOG.error("Failed to save Fabric config", e);
        }
    }
}