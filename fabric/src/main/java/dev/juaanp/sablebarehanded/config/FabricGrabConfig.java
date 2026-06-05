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
        public double rotationStabilization = 0.1;
        public boolean preventFastRotations = true;
        public boolean pivotAtGrabPoint = true;
        public boolean creativeSuperStrength = true;

        public double strength1Multiplier = 1.5;
        public double strength2Multiplier = 2.0;

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
        public boolean ignoreCollisionsGrabSelf = true;

        public boolean ignoreCollisionsRotationEverything = false;
        public boolean ignoreCollisionsRotationEntities = false;
        public boolean ignoreCollisionsRotationOtherPlayers = false;
        public boolean ignoreCollisionsRotationSelf = false;
    }

    public static class Client {
        public double rotationSensitivity = 0.7;
        public boolean invertRotation = false;
    }

    public static Common COMMON = new Common();
    public static Client CLIENT = new Client();

    public static void load() {
        try {
            if (FILE.exists()) {
                try (FileReader reader = new FileReader(FILE)) {
                    FabricGrabConfig loaded = GSON.fromJson(reader, FabricGrabConfig.class);
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
            GSON.toJson(FabricGrabConfig.class, writer);
        } catch (Exception e) {
            Constants.LOG.error("Failed to save Fabric config", e);
        }
    }
}