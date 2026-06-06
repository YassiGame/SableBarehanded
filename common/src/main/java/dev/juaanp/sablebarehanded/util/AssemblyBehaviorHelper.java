package dev.juaanp.sablebarehanded.util;

import dev.juaanp.sablebarehanded.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyBehaviorHelper {

    public static boolean isIgnored(Level level, BlockPos pos, BlockState state) {
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) return true;

        if (!state.getFluidState().isEmpty() && !state.isSolidRender(level, pos)) return true;

        return false;
    }

    public static boolean isLiftableDecor(BlockState state) {
        Block block = state.getBlock();
        return block instanceof BushBlock;
    }

    public static boolean isFastLift(Level level, BlockPos pos, BlockState state) {
        return state.hasBlockEntity() && !state.isCollisionShapeFullBlock(level, pos);
    }

    public static java.util.List<BlockPos> getConnectedBlocks(Level level, BlockPos pos) {
        java.util.List<BlockPos> blocks = new java.util.ArrayList<>();
        blocks.add(pos);
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof net.minecraft.world.level.block.ChestBlock) {
            var type = state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE);
            if (type != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
                blocks.add(pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state)));
            }
        }
        else if (isLiftableDecor(level.getBlockState(pos.above()))) {
            blocks.add(pos.above());
        } else if (isLiftableDecor(state)) {
            blocks.add(pos.below());
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