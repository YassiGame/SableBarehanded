package dev.juaanp.sablebarehanded.util;

import net.minecraft.core.BlockPos;
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
}