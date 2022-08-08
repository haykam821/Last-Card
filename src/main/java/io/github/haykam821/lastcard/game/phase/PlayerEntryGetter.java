package io.github.haykam821.lastcard.game.phase;

import io.github.haykam821.lastcard.game.player.PlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerEntryGetter {
	public PlayerEntry getPlayerEntry(ServerPlayerEntity player);
}
