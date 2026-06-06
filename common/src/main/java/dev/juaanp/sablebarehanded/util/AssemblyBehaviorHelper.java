package dev.juaanp.sablebarehanded.util;

import dev.juaanp.sablebarehanded.config.CommonConfig;
import dev.juaanp.sablebarehanded.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

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
        blocks.add(pos);

        BlockState baseState = level.getBlockState(pos);

        if (baseState.getBlock() instanceof ChestBlock) {
            var type = baseState.getValue(ChestBlock.TYPE);
            if (type != ChestType.SINGLE) {
                blocks.add(pos.relative(ChestBlock.getConnectedDirection(baseState)));
            }
        }
        else if (baseState.getBlock() instanceof DoorBlock) {
            var half = baseState.getValue(DoorBlock.HALF);
            if (half == DoubleBlockHalf.LOWER) {
                blocks.add(pos.above());
            } else {
                blocks.add(pos.below());
            }
        }
        else if (baseState.getBlock() instanceof BedBlock) {
            var part = baseState.getValue(BedBlock.PART);
            var facing = baseState.getValue(BedBlock.FACING);
            if (part == BedPart.HEAD) {
                blocks.add(pos.relative(facing.getOpposite()));
            } else {
                blocks.add(pos.relative(facing));
            }
        }

        java.util.Set<BlockPos> assembly = new java.util.HashSet<>(blocks);
        java.util.Queue<BlockPos> queue = new java.util.LinkedList<>(blocks);

        LevelReader simulatedLevel = (LevelReader) Proxy.newProxyInstance(
                LevelReader.class.getClassLoader(),
                new Class<?>[] { LevelReader.class },
                (proxy, method, args) -> {
                    if (args != null && args.length > 0 && args[0] instanceof BlockPos) {
                        BlockPos p = (BlockPos) args[0];
                        if (assembly.contains(p)) {
                            String name = method.getName();
                            if (name.equals("getBlockState")) {
                                return Blocks.AIR.defaultBlockState();
                            }
                            if (name.equals("getFluidState")) {
                                return Fluids.EMPTY.defaultFluidState();
                            }
                            if (name.equals("getBlockEntity")) {
                                return null;
                            }
                        }
                    }
                    try {
                        return method.invoke(level, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
        );

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                BlockPos adj = current.relative(dir);
                if (assembly.contains(adj)) continue;

                BlockState adjState = level.getBlockState(adj);

                if (adjState.isAir() || !isLiftableDecor(level, adj, adjState)) continue;

                if (!adjState.canSurvive(simulatedLevel, adj)) {
                    assembly.add(adj);
                    queue.add(adj);
                }
            }
        }

        return new java.util.ArrayList<>(assembly);
    }

    public static int calculateAssemblyTicks(Player player, Level level, java.util.List<BlockPos> blocks) {
        boolean isCreativeSuper = player.isCreative() && CommonConfig.COMMON.creativeSuperStrength;
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
        var strengthEffect = player.getEffect(MobEffects.DAMAGE_BOOST);
        if (strengthEffect != null) {
            int amp = strengthEffect.getAmplifier();
            strengthMulti = amp == 0 ? CommonConfig.COMMON.strength1Multiplier : CommonConfig.COMMON.strength2Multiplier;
        }

        return (int) Math.max(1, (totalTicks / strengthMulti) / CommonConfig.COMMON.barehandedAssemblySpeedMultiplier);
    }
}