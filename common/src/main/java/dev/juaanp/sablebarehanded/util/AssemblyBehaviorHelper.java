package dev.juaanp.sablebarehanded.util;

import dev.juaanp.sablebarehanded.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyBehaviorHelper {

    public static boolean isIgnored(Level level, BlockPos pos, BlockState state) {
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) return true;
        if (!state.getFluidState().isEmpty() && !state.isSolidRender(level, pos)) return true;
        return false;
    }

    public static boolean isLiftableDecor(Level level, BlockPos pos, BlockState state) {
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F || !state.getFluidState().isEmpty()) return false;
        return state.getCollisionShape(level, pos).isEmpty();
    }

    public static boolean isFastLift(Level level, BlockPos pos, BlockState state) {
        return state.hasBlockEntity() && !state.isCollisionShapeFullBlock(level, pos);
    }

    public static java.util.List<BlockPos> getConnectedBlocks(Level level, BlockPos pos) {
        java.util.List<BlockPos> blocks = new java.util.ArrayList<>();

        BlockPos basePos = pos;

        while (isLiftableDecor(level, basePos, level.getBlockState(basePos))) {
            basePos = basePos.below();
        }

        blocks.add(basePos);
        BlockState baseState = level.getBlockState(basePos);

        if (baseState.getBlock() instanceof net.minecraft.world.level.block.ChestBlock) {
            var type = baseState.getValue(net.minecraft.world.level.block.ChestBlock.TYPE);
            if (type != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
                blocks.add(basePos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(baseState)));
            }
        }
        else if (baseState.getBlock() instanceof net.minecraft.world.level.block.DoorBlock) {
            var half = baseState.getValue(net.minecraft.world.level.block.DoorBlock.HALF);
            if (half == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER) {
                blocks.add(basePos.above());
            } else {
                blocks.add(basePos.below());
            }
        }
        else if (baseState.getBlock() instanceof net.minecraft.world.level.block.BedBlock) {
            var part = baseState.getValue(net.minecraft.world.level.block.BedBlock.PART);
            var facing = baseState.getValue(net.minecraft.world.level.block.BedBlock.FACING);
            if (part == net.minecraft.world.level.block.state.properties.BedPart.HEAD) {
                blocks.add(basePos.relative(facing.getOpposite()));
            } else {
                blocks.add(basePos.relative(facing));
            }
        }

        BlockPos currentUp = basePos.above();
        while (isLiftableDecor(level, currentUp, level.getBlockState(currentUp))) {
            if (!blocks.contains(currentUp)) {
                blocks.add(currentUp);
            }
            currentUp = currentUp.above();
        }

        return blocks;
    }

    public static int calculateAssemblyTicks(Player player, Level level, java.util.List<BlockPos> blocks) {
        boolean isCreativeSuper = player.isCreative() && Services.CONFIG.creativeSuperStrength();
        if (isCreativeSuper) return 1;

        int totalTicks = 0;
        for (BlockPos pos : blocks) {
            BlockState state = level.getBlockState(pos);

            if (isFastLift(level, pos, state)) {
                totalTicks += 2;
            } else {
                float progressPerTick = state.getDestroyProgress(player, level, pos);
                if (progressPerTick <= 0.0F) return Integer.MAX_VALUE;

                int vanillaTicks = (int) Math.ceil(1.0F / progressPerTick);
                totalTicks += vanillaTicks;
            }
        }

        double strengthMulti = 1.0;
        var strengthEffect = player.getEffect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST);
        if (strengthEffect != null) {
            int amp = strengthEffect.getAmplifier();
            strengthMulti = amp == 0 ? Services.CONFIG.strength1Multiplier() : Services.CONFIG.strength2Multiplier();
        }

        return (int) Math.max(1, (totalTicks / strengthMulti) / Services.CONFIG.barehandedAssemblySpeedMultiplier());
    }
}