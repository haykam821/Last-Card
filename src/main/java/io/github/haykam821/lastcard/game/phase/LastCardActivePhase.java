package io.github.haykam821.lastcard.game.phase;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.lastcard.Main;
import io.github.haykam821.lastcard.card.CardDeck;
import io.github.haykam821.lastcard.game.LastPlayedBar;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import io.github.haykam821.lastcard.turn.TurnManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameActivity;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.item.ItemPickupEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;
import xyz.nucleoid.stimuli.event.world.FluidFlowEvent;

public class LastCardActivePhase implements GameActivityEvents.Enable, GameActivityEvents.Tick, GamePlayerEvents.Add, PlayerDamageEvent, PlayerDeathEvent, GamePlayerEvents.Remove, ItemUseEvent, FluidFlowEvent, ItemPickupEvent {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final LastCardMap map;
	private final LastPlayedBar bar;
	private final List<PlayerEntry> players;
	private final CardDeck deck = new CardDeck();
	private final TurnManager turnManager = new TurnManager(this);
	private boolean singleplayer;
	private boolean opened;

	public LastCardActivePhase(GameSpace gameSpace, ServerWorld world, LastCardMap map, GlobalWidgets widgets) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;

		this.bar = new LastPlayedBar(this, widgets);

		int playerCount = this.gameSpace.getPlayers().size();
		this.players = new ArrayList<>(playerCount);
		this.singleplayer = playerCount == 1;
	}

	public static void open(GameSpace gameSpace, ServerWorld world, LastCardMap map) {
		gameSpace.setActivity(activity -> {
			GlobalWidgets widgets = GlobalWidgets.addTo(activity);
			LastCardActivePhase phase = new LastCardActivePhase(gameSpace, world, map, widgets);

			LastCardActivePhase.setRules(activity);

			// Listeners
			activity.listen(GameActivityEvents.ENABLE, phase);
			activity.listen(GameActivityEvents.TICK, phase);
			activity.listen(GamePlayerEvents.ADD, phase);
			activity.listen(PlayerDamageEvent.EVENT, phase);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GamePlayerEvents.REMOVE, phase);
			activity.listen(ItemUseEvent.EVENT, phase);
			activity.listen(FluidFlowEvent.EVENT, phase);
			activity.listen(ItemPickupEvent.EVENT, phase);
		});
	}

	// Listeners

	@Override
	public void onEnable() {
		this.opened = true;

		// Angle calculation
		int index = 0;
		int size = gameSpace.getPlayers().size() + 1;

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
			player.changeGameMode(GameMode.SPECTATOR);
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
	public TypedActionResult<ItemStack> onUse(ServerPlayerEntity player, Hand hand) {
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

	@Override
	public ActionResult onFluidFlow(ServerWorld world, BlockPos fluidPos, BlockState fluidBlock, Direction flowDirection, BlockPos flowTo, BlockState flowToBlock) {
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult onPickupItem(ServerPlayerEntity player, ItemEntity entity, ItemStack stack) {
		return ActionResult.FAIL;
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
		return this.players.get(Math.floorMod(index, this.players.size()));
	}

	public ServerWorld getWorld() {
		return this.world;
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

	protected static void setRules(GameActivity activity) {
		activity.deny(GameRuleType.BLOCK_DROPS);
		activity.deny(GameRuleType.BREAK_BLOCKS);
		activity.deny(GameRuleType.CRAFTING);
		activity.deny(GameRuleType.DISMOUNT_VEHICLE);
		activity.deny(GameRuleType.FALL_DAMAGE);
		activity.deny(GameRuleType.HUNGER);
		activity.deny(GameRuleType.INTERACTION);
		activity.deny(GameRuleType.MODIFY_ARMOR);
		activity.deny(GameRuleType.PLACE_BLOCKS);
		activity.deny(GameRuleType.PORTALS);
		activity.deny(GameRuleType.PVP);
		activity.deny(GameRuleType.THROW_ITEMS);
	}

	protected static void spawn(ServerWorld world, LastCardMap map, ServerPlayerEntity player) {
		Vec3d podium = map.getPodium();
		player.teleport(world, podium.getX(), podium.getY(), podium.getZ(), 0, 0);
	}
}