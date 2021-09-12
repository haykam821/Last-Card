package io.github.haykam821.lastcard.game.phase;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.lastcard.Main;
import io.github.haykam821.lastcard.card.CardDeck;
import io.github.haykam821.lastcard.game.LastPlayedBar;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import io.github.haykam821.lastcard.turn.TurnManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameLogic;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.event.UseItemListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public class LastCardActivePhase implements GameOpenListener, GameTickListener, PlayerAddListener, PlayerDamageListener, PlayerDeathListener, PlayerRemoveListener, UseItemListener {
	private final GameSpace gameSpace;
	private final LastCardMap map;
	private final LastPlayedBar bar;
	private final List<PlayerEntry> players;
	private final CardDeck deck = new CardDeck();
	private final TurnManager turnManager = new TurnManager(this);
	private boolean singleplayer;
	private boolean opened;

	public LastCardActivePhase(GameSpace gameSpace, LastCardMap map, GlobalWidgets widgets) {
		this.gameSpace = gameSpace;
		this.map = map;

		this.bar = new LastPlayedBar(this, widgets);

		int playerCount = this.gameSpace.getPlayerCount();
		this.players = new ArrayList<>(playerCount);
		this.singleplayer = playerCount == 1;
	}

	public static void open(GameSpace gameSpace, LastCardMap map) {
		gameSpace.openGame(game -> {
			GlobalWidgets widgets = new GlobalWidgets(game);
			LastCardActivePhase phase = new LastCardActivePhase(gameSpace, map, widgets);

			LastCardActivePhase.setRules(game);

			// Listeners
			game.on(GameOpenListener.EVENT, phase);
			game.on(GameTickListener.EVENT, phase);
			game.on(PlayerAddListener.EVENT, phase);
			game.on(PlayerDamageListener.EVENT, phase);
			game.on(PlayerDeathListener.EVENT, phase);
			game.on(PlayerRemoveListener.EVENT, phase);
			game.on(UseItemListener.EVENT, phase);
		});
	}

	// Listeners

	@Override
	public void onOpen() {
		this.opened = true;

		// Angle calculation
		int index = 0;
		int size = gameSpace.getPlayerCount() + 1;

		Vec3d podium = this.map.getPodium();
		double distance = this.map.getPodiumDistance();

		for (ServerPlayerEntity player : gameSpace.getPlayers()) {
			double theta = (index / (float) size) * 2 * Math.PI;
			double x = podium.getX() + Math.sin(theta) * distance;
			double z = podium.getZ() - Math.cos(theta) * distance;
			Vec3d home = new Vec3d(x, podium.getY(), z);

			PlayerEntry entry = new PlayerEntry(this, player, home);

			if (index == 0) {
				this.turnManager.setTurn(entry);
			}
			this.players.add(entry);

			entry.spawn();
			index += 1;
		}

		this.turnManager.sendNextTurnMessage();
		this.updateBar();
	}

	@Override
	public void onTick() {
		for (PlayerEntry player : this.players) {
			player.tick();
		}

		// End early if there are not enough players to continue
		if (this.shouldEndEarly()) {
			this.endWithMessage(this.getEndingMessage());
		}
	}

	@Override
	public void onAddPlayer(ServerPlayerEntity player) {
		if (this.opened) {
			player.setGameMode(GameMode.SPECTATOR);
		}
	}

	@Override
	public ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult onDeath(ServerPlayerEntity player, DamageSource source) {
		LastCardActivePhase.spawn(this.getWorld(), this.map, player);
		return ActionResult.FAIL;
	}

	@Override
	public void onRemovePlayer(ServerPlayerEntity player) {
		if (!this.opened) return;

		PlayerEntry entry = this.getPlayerEntry(player);
		if (entry == null) return;

		// Skip turn
		if (this.turnManager.hasTurn(entry)) {
			this.turnManager.cycleTurn();
		}
		this.players.remove(entry);
	}

	@Override
	public TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == Main.CARD_HAND) {
			PlayerEntry entry = this.getPlayerEntry(player);
			if (entry != null) {
				entry.openCardHand();
				return TypedActionResult.success(stack);
			}
		}
		
		return TypedActionResult.pass(stack);
	}

	// Utilities

	public void updateBar() {
		this.bar.update();
	}

	private boolean shouldEndEarly() {
		if (this.singleplayer) {
			return this.players.isEmpty();
		} else {
			return this.players.size() <= 1;
		}
	}

	private Text getEndingMessage() {
		if (this.players.isEmpty()) {
			return new TranslatableText("text.lastcard.no_winners").formatted(Formatting.GOLD);
		}

		PlayerEntry winner = this.players.iterator().next();
		return winner.getWinMessage();
	}

	public void endWithWinner(PlayerEntry player) {
		this.endWithMessage(player.getWinMessage());
	}
	
	private void endWithMessage(Text message) {
		this.sendMessage(message);
		this.gameSpace.close(GameCloseReason.FINISHED);
		this.opened = false;
	}

	private PlayerEntry getPlayerEntry(ServerPlayerEntity player) {
		for (PlayerEntry entry : this.players) {
			if (player == entry.getPlayer()) {
				return entry;
			}
		}
		return null;
	}

	public PlayerEntry getPlayerEntry(int index) {
		return this.players.get(index % this.players.size());
	}

	public ServerWorld getWorld() {
		return this.gameSpace.getWorld();
	}

	public LastCardMap getMap() {
		return this.map;
	}

	public void sendMessage(Text message) {
		this.gameSpace.getPlayers().sendMessage(message);
	}

	public void sendMessageWithException(Text message, PlayerEntry exception, Text exceptionMessage) {
		for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			if (player == exception.getPlayer()) {
				player.sendMessage(exceptionMessage, false);
			} else {
				player.sendMessage(message, false);
			}
		}
	}

	public List<PlayerEntry> getPlayers() {
		return this.players;
	}

	public CardDeck getDeck() {
		return this.deck;
	}

	public TurnManager getTurnManager() {
		return this.turnManager;
	}

	protected static void setRules(GameLogic game) {
		game.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
		game.setRule(GameRule.BREAK_BLOCKS, RuleResult.DENY);
		game.setRule(GameRule.CRAFTING, RuleResult.DENY);
		game.setRule(GameRule.DISMOUNT_VEHICLE, RuleResult.DENY);
		game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
		game.setRule(GameRule.FLUID_FLOW, RuleResult.DENY);
		game.setRule(GameRule.HUNGER, RuleResult.DENY);
		game.setRule(GameRule.INTERACTION, RuleResult.DENY);
		game.setRule(GameRule.MODIFY_ARMOR, RuleResult.DENY);
		game.setRule(GameRule.PICKUP_ITEMS, RuleResult.DENY);
		game.setRule(GameRule.PLACE_BLOCKS, RuleResult.DENY);
		game.setRule(GameRule.PORTALS, RuleResult.DENY);
		game.setRule(GameRule.PVP, RuleResult.DENY);
		game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
	}

	protected static void spawn(ServerWorld world, LastCardMap map, ServerPlayerEntity player) {
		Vec3d podium = map.getPodium();
		player.teleport(world, podium.getX(), podium.getY(), podium.getZ(), 0, 0);
	}
}