package dev.juaanp.sablebarehanded.physics;

import dev.juaanp.sablebarehanded.api.SableBarehandedEvents;
import dev.juaanp.sablebarehanded.config.ServerConfig;
import dev.juaanp.sablebarehanded.platform.Services;
import dev.juaanp.sablebarehanded.util.AssemblyBehaviorHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.List;

public class GrabActionHandler {

    public static void startGrabbing(Player player, BlockPos pos) {
        Level level = player.level();
        if (level.isClientSide()) return;

        if (ServerGrabManager.isPlayerGrabbing(player) || !player.getMainHandItem().isEmpty()) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        double reach = GrabPhysicsController.getGrabReach(player);
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > (reach * reach)) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        SubLevel target = Sable.HELPER.getContaining(level, pos);
        if (!(target instanceof ServerSubLevel serverSubLevel)) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        Vector3d localGrabBlock = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        performGrab(player, serverSubLevel, localGrabBlock);
        SableBarehandedEvents.fireOnGrab(player, serverSubLevel);
    }

    public static void assembleAndGrab(Player player, BlockPos pos) {
        Level level = player.level();
        if (level.isClientSide()) return;

        if (ServerGrabManager.isPlayerGrabbing(player) || !player.getMainHandItem().isEmpty()) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        double reach = GrabPhysicsController.getGrabReach(player);
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > (reach * reach)) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        double maxDist = ServerConfig.INSTANCE.barehandedAssemblyMaxDistance + ServerConfig.INSTANCE.assemblyServerDistanceTolerance;
        if (player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > (maxDist * maxDist)) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        BlockState mainState = level.getBlockState(pos);
        if (AssemblyBehaviorHelper.isIgnored(level, pos, mainState)) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        List<BlockPos> blocks = AssemblyBehaviorHelper.getConnectedBlocks(level, pos);

        if (!SableBarehandedEvents.fireBeforeAssemble(player, pos, blocks)) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        for (BlockPos bPos : blocks) {
            if (Sable.HELPER.getContaining(level, bPos) != null) {
                Services.NETWORK.sendStopGrabbingAnimation(player);
                return;
            }
        }

        BoundingBox3i bounds = BoundingBox3i.from(blocks);
        SubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks((ServerLevel) level, pos, blocks, bounds);

        if (subLevel instanceof ServerSubLevel serverSubLevel) {
            SableBarehandedEvents.fireOnAssemble(player, serverSubLevel, blocks);

            boolean isFastLift = AssemblyBehaviorHelper.isFastLift(level, pos, mainState);

            if (!isFastLift) {
                net.minecraft.world.level.block.SoundType soundType = mainState.getSoundType();
                level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                level.levelEvent(2001, pos, net.minecraft.world.level.block.Block.getId(mainState));
            } else {
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 0.5f);
            }

            for (BlockPos bPos : blocks) {
                level.updateNeighborsAt(bPos, net.minecraft.world.level.block.Blocks.AIR);
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    BlockPos neighbor = bPos.relative(dir);
                    level.neighborChanged(neighbor, net.minecraft.world.level.block.Blocks.AIR, bPos);

                    net.minecraft.world.level.material.FluidState fluid = level.getFluidState(neighbor);
                    if (!fluid.isEmpty()) {
                        level.scheduleTick(neighbor, fluid.getType(), fluid.getType().getTickDelay(level));
                    }

                    net.minecraft.world.level.block.state.BlockState neighborState = level.getBlockState(neighbor);
                    if (neighborState.getBlock() instanceof net.minecraft.world.level.block.FallingBlock) {
                        level.scheduleTick(neighbor, neighborState.getBlock(), 2);
                    }
                }
            }

            ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(level);

            if (container != null) {
                container.physicsSystem().getPipeline().wakeUp(serverSubLevel);
            }

            Vector3d globalVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vector3d localGrabBlock = serverSubLevel.logicalPose().transformPositionInverse(globalVec, new Vector3d());

            performGrab(player, serverSubLevel, localGrabBlock);
            SableBarehandedEvents.fireOnGrab(player, serverSubLevel);
        } else {
            Services.NETWORK.sendStopGrabbingAnimation(player);
        }
    }

    private static void performGrab(Player player, ServerSubLevel serverSubLevel, Vector3d localGrabBlock) {
        Level level = player.level();
        ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(level);
        if (container == null) return;

        var pipeline = container.physicsSystem().getPipeline();

        org.joml.Vector3dc com = serverSubLevel.getMassTracker().getCenterOfMass();
        if (com == null || serverSubLevel.getMassTracker().getMass() <= ServerConfig.INSTANCE.minPhysicsMass) {
            Services.NETWORK.sendStopGrabbingAnimation(player);
            return;
        }

        Vector3d localCenterOfMass = new Vector3d(com);

        Vector3d globalGrabBlockPos = serverSubLevel.logicalPose().transformPosition(new Vector3d(localGrabBlock));
        float distance = (float) player.getEyePosition().distanceTo(new Vec3(globalGrabBlockPos.x, globalGrabBlockPos.y, globalGrabBlockPos.z));

        Vector3d crosshairTarget = JOMLConversion.toJOML(player.getEyePosition().add(player.getLookAngle().scale(Math.max(ServerConfig.INSTANCE.minDistance, distance))));
        Quaterniond initialOrient = serverSubLevel.logicalPose().orientation();

        GrabSession session = new GrabSession(serverSubLevel, distance, localGrabBlock, localCenterOfMass, crosshairTarget, initialOrient, pipeline);

        pipeline.wakeUp(serverSubLevel);

        GrabPhysicsController.rebuildConstraint(session);
        Services.NETWORK.sendStartGrabbingAnimation(player);
        Services.NETWORK.sendSyncGrabState(player,
                com != null ? serverSubLevel.getMassTracker().getMass() : 0.0,
                serverSubLevel.getUniqueId(),
                localGrabBlock,
                distance
        );
        ServerGrabManager.registerGrab(player, session);
    }
}