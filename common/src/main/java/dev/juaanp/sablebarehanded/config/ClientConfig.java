package dev.juaanp.sablebarehanded.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.juaanp.sablebarehanded.Constants;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

public class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = Paths.get("config", Constants.MOD_ID + "-client.json").toFile();

    public double verticalRotationSensitivity = 0.5;
    public double horizontalRotationSensitivity = 0.5;
    public boolean invertVerticalRotation = false;
    public boolean invertHorizontalRotation = false;
    public boolean rotateAroundCenter = false;
    public boolean preventMovementWhileRotating = true;

    public double armTransitionSpeed = 0.14;
    public double armGrabLowerOffset = 1.5;
    public double assemblyShakeMultiplier = 0.04;
    public double shakeFrequencyX = 3.0;
    public double shakeFrequencyY = 4.0;
    public double shakeFrequencyZ = 5.0;
    public double armEaseFullThreshold = 0.99;
    public double visualShakeThreshold = 0.3;

    public boolean hideHandsWhileGrabbing = false;
    public double grabArmOffsetX = 0.18;
    public double grabArmOffsetY = -0.6;
    public double grabArmOffsetZ = -0.2;

    public boolean preventAssemblyWhenMining = true;
    public double barehandedAssemblyMiningThreshold = 0.08;

    public int regrabDebounceTicks = 2;

    public static ClientConfig INSTANCE = new ClientConfig();

    public static void load() {
        try {
            if (FILE.exists()) {
                try (FileReader reader = new FileReader(FILE)) {
                    ClientConfig loaded = GSON.fromJson(reader, ClientConfig.class);
                    if (loaded != null) {
                        INSTANCE = loaded;
                    }
                }
            } else {
                save();
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to load client config", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (Exception e) {
            Constants.LOG.error("Failed to save client config", e);
        }
    }
}