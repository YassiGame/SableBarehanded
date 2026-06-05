package dev.juaanp.sablebarehanded.client; // <-- Verifica tu package

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.juaanp.sablebarehanded.config.FabricGrabConfig; // <-- Verifica tu import
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("config.sable-barehanded.title"));

            builder.setSavingRunnable(FabricGrabConfig::save);

            ConfigCategory physics = builder.getOrCreateCategory(Component.translatable("sable-barehanded.config.physics.category"));
            ConfigCategory client = builder.getOrCreateCategory(Component.translatable("sable-barehanded.config.client.category"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            SubCategoryBuilder mechanics = entryBuilder.startSubCategory(Component.translatable("sable-barehanded.config.physics.mechanics"));

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.max_force"), FabricGrabConfig.COMMON.maxForce)
                    .setDefaultValue(120.0).setMin(1.0).setMax(10000000.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.maxForce = v).build());

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.min_distance"), FabricGrabConfig.COMMON.minDistance)
                    .setDefaultValue(2.0).setMin(0.1).setMax(10.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.minDistance = v).build());

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.grab_stabilization"), FabricGrabConfig.COMMON.grabStabilization)
                    .setDefaultValue(0.01).setMin(0.0).setMax(1.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.grabStabilization = v).build());

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.rotation_stabilization"), FabricGrabConfig.COMMON.rotationStabilization)
                    .setDefaultValue(0.5).setMin(0.0).setMax(1.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.rotationStabilization = v).build());

            mechanics.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.enable_rotation"), FabricGrabConfig.COMMON.enableRotation)
                    .setDefaultValue(true).setSaveConsumer(v -> FabricGrabConfig.COMMON.enableRotation = v).build());

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.max_rotation_speed"), FabricGrabConfig.COMMON.maxRotationSpeed)
                    .setDefaultValue(0.2).setMin(0.0).setMax(1.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.maxRotationSpeed = v).build());

            mechanics.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.prevent_fast_rotations"), FabricGrabConfig.COMMON.preventFastRotations)
                    .setDefaultValue(true).setSaveConsumer(v -> FabricGrabConfig.COMMON.preventFastRotations = v).build());

            mechanics.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.creative_super_strength"), FabricGrabConfig.COMMON.creativeSuperStrength)
                    .setDefaultValue(true).setTooltip(Component.translatable("sable-barehanded.config.physics.creative_super_strength.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.COMMON.creativeSuperStrength = v).build());

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.strength_1_multiplier"), FabricGrabConfig.COMMON.strength1Multiplier)
                    .setDefaultValue(2.0).setMin(1.0).setMax(100.0)
                    .setTooltip(Component.translatable("sable-barehanded.config.physics.strength_multiplier.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.COMMON.strength1Multiplier = v).build());

            mechanics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.strength_2_multiplier"), FabricGrabConfig.COMMON.strength2Multiplier)
                    .setDefaultValue(4.0).setMin(1.0).setMax(100.0)
                    .setTooltip(Component.translatable("sable-barehanded.config.physics.strength_multiplier.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.COMMON.strength2Multiplier = v).build());

            physics.addEntry(mechanics.build());

            SubCategoryBuilder grabCollisions = entryBuilder.startSubCategory(Component.translatable("sable-barehanded.config.physics.grab_collisions"));

            grabCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_self"), FabricGrabConfig.COMMON.ignoreCollisionsGrabSelf)
                    .setDefaultValue(false).setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsGrabSelf = v).build());

            grabCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_other_players"), FabricGrabConfig.COMMON.ignoreCollisionsGrabOtherPlayers)
                    .setDefaultValue(false).setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsGrabOtherPlayers = v).build());

            grabCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_entities"), FabricGrabConfig.COMMON.ignoreCollisionsGrabEntities)
                    .setDefaultValue(false).setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsGrabEntities = v).build());

            grabCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_everything"), FabricGrabConfig.COMMON.ignoreCollisionsGrabEverything)
                    .setDefaultValue(false).setTooltip(Component.translatable("sable-barehanded.config.physics.ignore_collisions_everything.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsGrabEverything = v).build());

            physics.addEntry(grabCollisions.build());

            SubCategoryBuilder rotationCollisions = entryBuilder.startSubCategory(Component.translatable("sable-barehanded.config.physics.rotation_collisions"));

            rotationCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_self"), FabricGrabConfig.COMMON.ignoreCollisionsRotationSelf)
                    .setDefaultValue(true).setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsRotationSelf = v).build());

            rotationCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_other_players"), FabricGrabConfig.COMMON.ignoreCollisionsRotationOtherPlayers)
                    .setDefaultValue(true).setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsRotationOtherPlayers = v).build());

            rotationCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_entities"), FabricGrabConfig.COMMON.ignoreCollisionsRotationEntities)
                    .setDefaultValue(true).setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsRotationEntities = v).build());

            rotationCollisions.add(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.physics.ignore_collisions_everything"), FabricGrabConfig.COMMON.ignoreCollisionsRotationEverything)
                    .setDefaultValue(false).setTooltip(Component.translatable("sable-barehanded.config.physics.ignore_collisions_everything.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.COMMON.ignoreCollisionsRotationEverything = v).build());

            physics.addEntry(rotationCollisions.build());

            SubCategoryBuilder corePhysics = entryBuilder.startSubCategory(Component.translatable("sable-barehanded.config.physics.core_physics"));

            corePhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.stiffness"), FabricGrabConfig.COMMON.stiffness)
                    .setDefaultValue(1000.0).setMin(1.0).setMax(100000.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.stiffness = v).build());

            corePhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.damping"), FabricGrabConfig.COMMON.damping)
                    .setDefaultValue(125.0).setMin(1.0).setMax(10000.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.damping = v).build());

            corePhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.angular_damping"), FabricGrabConfig.COMMON.angularDamping)
                    .setDefaultValue(850.0).setMin(1.0).setMax(10000.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.angularDamping = v).build());

            physics.addEntry(corePhysics.build());

            SubCategoryBuilder advancedPhysics = entryBuilder.startSubCategory(Component.translatable("sable-barehanded.config.physics.advanced_physics"));

            advancedPhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.rotation_mass_damping"), FabricGrabConfig.COMMON.rotationMassDampingFactor)
                    .setDefaultValue(0.02).setSaveConsumer(v -> FabricGrabConfig.COMMON.rotationMassDampingFactor = v).build());

            advancedPhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.tension_suspend_threshold"), FabricGrabConfig.COMMON.tensionSuspendThreshold)
                    .setDefaultValue(3.5).setSaveConsumer(v -> FabricGrabConfig.COMMON.tensionSuspendThreshold = v).build());

            advancedPhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.tension_break_threshold"), FabricGrabConfig.COMMON.tensionBreakThreshold)
                    .setDefaultValue(9.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.tensionBreakThreshold = v).build());

            advancedPhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.max_player_velocity_y_up"), FabricGrabConfig.COMMON.maxPlayerVelocityYUp)
                    .setDefaultValue(2.5).setSaveConsumer(v -> FabricGrabConfig.COMMON.maxPlayerVelocityYUp = v).build());

            advancedPhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.max_player_velocity_y_down"), FabricGrabConfig.COMMON.maxPlayerVelocityYDown)
                    .setDefaultValue(-4.0).setSaveConsumer(v -> FabricGrabConfig.COMMON.maxPlayerVelocityYDown = v).build());

            advancedPhysics.add(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.physics.max_player_velocity_xz"), FabricGrabConfig.COMMON.maxPlayerVelocityXZ)
                    .setDefaultValue(2.5).setSaveConsumer(v -> FabricGrabConfig.COMMON.maxPlayerVelocityXZ = v).build());

            physics.addEntry(advancedPhysics.build());

            client.addEntry(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.client.vertical_rotation_sensitivity"), FabricGrabConfig.CLIENT.verticalRotationSensitivity)
                    .setDefaultValue(0.5).setMin(0.1).setMax(1.0).setSaveConsumer(v -> FabricGrabConfig.CLIENT.verticalRotationSensitivity = v).build());

            client.addEntry(entryBuilder.startDoubleField(Component.translatable("sable-barehanded.config.client.horizontal_rotation_sensitivity"), FabricGrabConfig.CLIENT.horizontalRotationSensitivity)
                    .setDefaultValue(0.5).setMin(0.1).setMax(1.0).setSaveConsumer(v -> FabricGrabConfig.CLIENT.horizontalRotationSensitivity = v).build());

            client.addEntry(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.client.invert_vertical_rotation"), FabricGrabConfig.CLIENT.invertVerticalRotation)
                    .setDefaultValue(false).setTooltip(Component.translatable("sable-barehanded.config.client.invert_vertical_rotation.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.CLIENT.invertVerticalRotation = v).build());

            client.addEntry(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.client.invert_horizontal_rotation"), FabricGrabConfig.CLIENT.invertHorizontalRotation)
                    .setDefaultValue(false).setTooltip(Component.translatable("sable-barehanded.config.client.invert_horizontal_rotation.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.CLIENT.invertHorizontalRotation = v).build());

            client.addEntry(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.client.rotate_around_center"), FabricGrabConfig.CLIENT.rotateAroundCenter)
                    .setDefaultValue(false).setTooltip(Component.translatable("sable-barehanded.config.client.rotate_around_center.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.CLIENT.rotateAroundCenter = v).build());

            client.addEntry(entryBuilder.startBooleanToggle(Component.translatable("sable-barehanded.config.client.prevent_movement_while_rotating"), FabricGrabConfig.CLIENT.preventMovementWhileRotating)
                    .setDefaultValue(true).setTooltip(Component.translatable("sable-barehanded.config.client.prevent_movement_while_rotating.tooltip"))
                    .setSaveConsumer(v -> FabricGrabConfig.CLIENT.preventMovementWhileRotating = v).build());

            return builder.build();
        };
    }
}