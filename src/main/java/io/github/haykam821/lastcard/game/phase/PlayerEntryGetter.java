package io.github.haykam821.lastcard.game.phase;

import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerEntryGetter {
	public AbstractPlayerEntry getPlayerEntry(ServerPlayerEntity player);

	public AbstractPlayerEntry getTurn();
}
