package dev.juaanp.sablebarehanded.config;

public class ConfigMetadata {
    public record DoubleSpec(double def, double min, double max, String tooltip) {
        public DoubleSpec(double def, double min, double max) { this(def, min, max, null); }
    }
    public record IntSpec(int def, int min, int max, String tooltip) {
        public IntSpec(int def, int min, int max) { this(def, min, max, null); }
    }
    public record BooleanSpec(boolean def, String tooltip) {
        public BooleanSpec(boolean def) { this(def, null); }
    }
}