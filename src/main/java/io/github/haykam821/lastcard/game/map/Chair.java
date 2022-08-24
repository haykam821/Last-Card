package io.github.haykam821.lastcard.game.map;

import java.util.Comparator;

import io.github.haykam821.lastcard.game.PlayerEntry;
import io.github.haykam821.lastcard.mixin.ArmorStandEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.map_templates.TemplateRegion;

public class Chair extends Spawn {
	public static final Comparator<PlayerEntry> TURN_ORDER_COMPARATOR = Comparator.comparingInt(player -> {
		return player.getChair().turnOrder;
	});

	private static final BlockState CHAIR_STATE = Blocks.DARK_OAK_STAIRS.getDefaultState();
	private static final double MOUNT_Y_OFFSET = 0.25;

	private final BlockPos blockPos;
	private final int turnOrder;

	private Entity mount; 

	public Chair(TemplateRegion region) {
		super(region);

		this.blockPos = new BlockPos(this.pos);
		this.turnOrder = LastCardRegions.getTurnOrder(region);
	}

	public boolean isAt(BlockPos pos) {
		return this.blockPos.equals(pos);
	}

	@Override
	public void teleport(ServerPlayerEntity player) {
		if (this.mount == null) {
			this.mount = this.createMount(player.getWorld());
		}

		if (player.getWorld().isAir(this.blockPos)) {
			Direction facing = Direction.fromRotation(this.rotation).getOpposite();
			BlockState state = CHAIR_STATE.with(StairsBlock.FACING, facing);

			player.getWorld().setBlockState(this.blockPos, state);
		}

		super.teleport(player);
		player.startRiding(this.mount, true);
	}

	private Entity createMount(ServerWorld world) {
		ArmorStandEntity mount = new ArmorStandEntity(world, this.pos.getX(), this.pos.getY() + MOUNT_Y_OFFSET, this.pos.getZ());
		mount.setYaw(this.rotation);

		mount.setInvisible(true);
		((ArmorStandEntityAccessor) mount).lastcard$setMarker(true);

		world.spawnEntity(mount);
		return mount;
	}
}
