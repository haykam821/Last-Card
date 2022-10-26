package io.github.haykam821.lastcard.game.phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import io.github.haykam821.lastcard.Main;
import io.github.haykam821.lastcard.card.CardDeck;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.pile.PileCardDisplay;
import io.github.haykam821.lastcard.card.display.pile.PrivatePileCardDisplay;
import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.LastPlayedBar;
import io.github.haykam821.lastcard.game.map.Chair;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import io.github.haykam821.lastcard.game.player.VirtualPlayerEntry;
import io.github.haykam821.lastcard.turn.TurnManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.game.GameActivity;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemPickupEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;
import xyz.nucleoid.stimuli.event.world.FluidFlowEvent;

public class LastCardActivePhase implements PlayerEntryGetter, GameActivityEvents.Destroy, GameActivityEvents.Enable, GameActivityEvents.Tick, GamePlayerEvents.Offer, PlayerDamageEvent, PlayerDeathEvent, GamePlayerEvents.Remove, FluidFlowEvent, ItemPickupEvent, BlockUseEvent {
	private static final GameTeamKey PLAYERS_KEY = new GameTeamKey("players");
	private static final GameTeam PLAYERS_TEAM = new GameTeam(PLAYERS_KEY, GameTeamConfig.builder()
		.setCollision(AbstractTeam.CollisionRule.NEVER)
		.setNameTagVisibility(AbstractTeam.VisibilityRule.NEVER)
		.build());

	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final LastCardConfig config;
	private final LastCardMap map;
	private final TeamManager teams;
	private final LastPlayedBar bar;
	private final List<AbstractPlayerEntry> players;
	private final CardDeck deck = new CardDeck();
	private final TurnManager turnManager;
	private final CardDisplay privatePileDisplay;
	private final CardDisplay publicPileDisplay;
	private boolean singleplayer;
	private boolean opened;

	public LastCardActivePhase(GameSpace gameSpace, ServerWorld world, LastCardConfig config, LastCardMap map, TeamManager teams, GlobalWidgets widgets) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.config = config;
		this.map = map;

		this.teams = teams;
		this.bar = new LastPlayedBar(this, widgets);

		int playerCount = this.gameSpace.getPlayers().size();
		this.players = new ArrayList<>(playerCount);
		this.singleplayer = playerCount == 1;

		TemplateRegion pileCardDisplay = this.map.getPileCardDisplay();
		Vec3d particleOrigin = pileCardDisplay.getBounds().centerBottom();

		this.privatePileDisplay = new PrivatePileCardDisplay(this.getDeck(), this, pileCardDisplay);
		this.publicPileDisplay = new PileCardDisplay(this.getDeck(), this, pileCardDisplay);

		this.turnManager = new TurnManager(this, particleOrigin, this.privatePileDisplay, this.publicPileDisplay);
	}

	public static void open(GameSpace gameSpace, ServerWorld world, LastCardConfig config, LastCardMap map) {
		gameSpace.setActivity(activity -> {
			TeamManager teams = TeamManager.addTo(activity);
			teams.addTeam(PLAYERS_TEAM);

			GlobalWidgets widgets = GlobalWidgets.addTo(activity);
			LastCardActivePhase phase = new LastCardActivePhase(gameSpace, world, config, map, teams, widgets);

			LastCardActivePhase.setRules(activity);
			activity.allow(GameRuleType.DISMOUNT_VEHICLE);

			// Listeners
			activity.listen(GameActivityEvents.DESTROY, phase);
			activity.listen(GameActivityEvents.ENABLE, phase);
			activity.listen(GameActivityEvents.TICK, phase);
			activity.listen(GamePlayerEvents.OFFER, phase);
			activity.listen(PlayerDamageEvent.EVENT, phase);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GamePlayerEvents.REMOVE, phase);
			activity.listen(FluidFlowEvent.EVENT, phase);
			activity.listen(ItemPickupEvent.EVENT, phase);
			activity.listen(BlockUseEvent.EVENT, phase);
		});
	}

	// Listeners
	@Override
	public void onDestroy(GameCloseReason reason) {
		this.privatePileDisplay.destroy();
		this.publicPileDisplay.destroy();
	}

	@Override
	public void onEnable() {
		this.opened = true;

		// Randomly assign chairs to players
		List<ServerPlayerEntity> players = Lists.newArrayList(gameSpace.getPlayers());
		Collections.shuffle(players);

		int index = 0;

		for (ServerPlayerEntity player : players) {
			TemplateRegion chair = this.map.getChair(index);
			TemplateRegion privateCardDisplay = this.map.getPrivateCardDisplay(index);
			TemplateRegion publicCardDisplay = this.map.getPublicCardDisplay(index);

			AbstractPlayerEntry entry = new PlayerEntry(this, player, chair, privateCardDisplay, publicCardDisplay);

			this.players.add(entry);
			this.publicPileDisplay.add(player);

			teams.addPlayerTo(player, PLAYERS_KEY);

			entry.spawn();
			index += 1;
		}

		Random random = this.world.getRandom();
		int virtualPlayers = this.config.getVirtualPlayers().get(random);

		for (int i = 0; i < virtualPlayers; i++) {
			TemplateRegion chair = this.map.getChair(index);
			TemplateRegion publicCardDisplay = this.map.getPublicCardDisplay(index);

			AbstractPlayerEntry entry = new VirtualPlayerEntry(this, chair, publicCardDisplay);
			this.players.add(entry);

			entry.spawn();
			index += 1;
		}

		// Sort players by turn order
		this.players.sort(Chair.TURN_ORDER_COMPARATOR);
		this.turnManager.setTurn(this.players.get(0));

		this.updatePileDisplay();
		
		for (AbstractPlayerEntry player : this.players) {
			for (ServerPlayerEntity viewer : this.gameSpace.getPlayers()) {
				player.addDisplay(viewer);
			}

			player.updateDisplays();
		}

		this.turnManager.sendNextTurnEffects();
		this.updateBar();
	}

	@Override
	public void onTick() {
		this.turnManager.tick();

		for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			if (!this.map.contains(player)) {
				this.spawn(player);
			}
		}

		// End early if there are not enough players to continue
		if (this.shouldEndEarly()) {
			this.endWithMessage(this.getEndingMessage());
		}
	}

	@Override
	public PlayerOfferResult onOfferPlayer(PlayerOffer offer) {
		return this.map.getWaitingSpawn().acceptOffer(offer, this.world, GameMode.SPECTATOR).and(() -> {
			this.publicPileDisplay.add(offer.player());

			for (AbstractPlayerEntry player : this.players) {
				player.addDisplay(offer.player());
			}
		});
	}

	@Override
	public ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult onDeath(ServerPlayerEntity player, DamageSource source) {
		this.spawn(player);
		return ActionResult.FAIL;
	}

	@Override
	public void onRemovePlayer(ServerPlayerEntity player) {
		if (!this.opened) return;

		AbstractPlayerEntry entry = this.getPlayerEntry(player);
		if (entry == null) return;

		this.privatePileDisplay.remove(player);
		this.publicPileDisplay.remove(player);

		for (ServerPlayerEntity viewer : this.gameSpace.getPlayers()) {
			entry.removeDisplay(viewer);
		}

		entry.destroyDisplays();

		// Skip turn
		if (this.turnManager.hasTurn(entry)) {
			this.turnManager.cycleTurn();
		}
		this.players.remove(entry);
	}

	@Override
	public ActionResult onFluidFlow(ServerWorld world, BlockPos fluidPos, BlockState fluidBlock, Direction flowDirection, BlockPos flowTo, BlockState flowToBlock) {
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult onPickupItem(ServerPlayerEntity player, ItemEntity entity, ItemStack stack) {
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult onUse(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		AbstractPlayerEntry entry = this.getPlayerEntry(player);
		
		if (entry != null) {
			Chair chair = entry.getChair();

			if (chair.isAt(hitResult.getBlockPos())) {
				chair.teleport(player);
			}
		}

		return ActionResult.FAIL;
	}

	// Utilities
	public void spawn(ServerPlayerEntity player) {
		AbstractPlayerEntry entry = this.getPlayerEntry(player);
		
		if (entry == null) {
			this.map.getWaitingSpawn().teleport(player);
		} else {
			entry.spawn();
		}
	}

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

		AbstractPlayerEntry winner = this.players.iterator().next();
		return winner.getWinMessage();
	}

	public void endWithWinner(AbstractPlayerEntry player) {
		this.endWithMessage(player.getWinMessage());
	}
	
	private void endWithMessage(Text message) {
		this.sendMessage(message);
		this.gameSpace.close(GameCloseReason.FINISHED);
		this.opened = false;
	}

	@Override
	public AbstractPlayerEntry getPlayerEntry(ServerPlayerEntity player) {
		for (AbstractPlayerEntry entry : this.players) {
			if (entry.isPlayer(player)) {
				return entry;
			}
		}
		return null;
	}

	public AbstractPlayerEntry getPlayerEntry(int index) {
		return this.players.get(Math.floorMod(index, this.players.size()));
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public LastCardConfig getConfig() {
		return this.config;
	}

	public LastCardMap getMap() {
		return this.map;
	}

	public void sendMessage(Text message) {
		this.gameSpace.getPlayers().sendMessage(message);
	}

	public void sendMessageWithException(Text message, AbstractPlayerEntry exception, Text exceptionMessage) {
		for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			if (exception.isPlayer(player)) {
				player.sendMessage(exceptionMessage, false);
			} else {
				player.sendMessage(message, false);
			}
		}
	}

	public List<AbstractPlayerEntry> getPlayers() {
		return this.players;
	}

	public CardDeck getDeck() {
		return this.deck;
	}

	public TurnManager getTurnManager() {
		return this.turnManager;
	}

	@Override
	public AbstractPlayerEntry getTurn() {
		return this.turnManager.getTurn();
	}

	public void updatePileDisplay() {
		this.privatePileDisplay.update();
		this.publicPileDisplay.update();
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
		activity.deny(Main.WITHER_ROSE_WITHER_EFFECT);
	}
}