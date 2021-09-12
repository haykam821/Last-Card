package io.github.haykam821.lastcard.game.phase;

import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.map.LastCardMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class LastCardWaitingPhase implements PlayerAddListener, PlayerDamageListener, PlayerDeathListener, RequestStartListener {
	private final GameSpace gameSpace;
	private final LastCardMap map;

	public LastCardWaitingPhase(GameSpace gameSpace, LastCardMap map) {
		this.gameSpace = gameSpace;
		this.map = map;
	}

	public static GameOpenProcedure open(GameOpenContext<LastCardConfig> context) {
		LastCardConfig config = context.getConfig();

		LastCardMapBuilder mapBuilder = new LastCardMapBuilder(config.getMapConfig());
		LastCardMap map = mapBuilder.create();

		BubbleWorldConfig worldConfig = new BubbleWorldConfig()
			.setGenerator(map.createGenerator(context.getServer()))
			.setDefaultGameMode(GameMode.ADVENTURE);

		return context.createOpenProcedure(worldConfig, game -> {
			LastCardWaitingPhase phase = new LastCardWaitingPhase(game.getSpace(), map);
			GameWaitingLobby.applyTo(game, config.getPlayerConfig());

			LastCardActivePhase.setRules(game);
			game.setRule(GameRule.MODIFY_INVENTORY, RuleResult.DENY);

			// Listeners
			game.on(PlayerAddListener.EVENT, phase);
			game.on(PlayerDamageListener.EVENT, phase);
			game.on(PlayerDeathListener.EVENT, phase);
			game.on(RequestStartListener.EVENT, phase);
		});
	}

	@Override
	public void onAddPlayer(ServerPlayerEntity player) {
		LastCardActivePhase.spawn(this.gameSpace.getWorld(), this.map, player);
	}

	@Override
	public ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult onDeath(ServerPlayerEntity player, DamageSource source) {
		LastCardActivePhase.spawn(this.gameSpace.getWorld(), this.map, player);
		return ActionResult.FAIL;
	}

	@Override
	public StartResult requestStart() {
		LastCardActivePhase.open(this.gameSpace, this.map);
		return StartResult.OK;
	}
}