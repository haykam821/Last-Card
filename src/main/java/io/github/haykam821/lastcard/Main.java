package io.github.haykam821.lastcard;

import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.item.CardHandItem;
import io.github.haykam821.lastcard.game.phase.LastCardWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.plasmid.game.GameType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "lastcard";
	public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).get();

	private static final Identifier LAST_CARD_ID = new Identifier(MOD_ID, "last_card");
	public static final GameType<LastCardConfig> LAST_CARD_TYPE = GameType.register(LAST_CARD_ID, LastCardConfig.CODEC, LastCardWaitingPhase::open);

	private static final Identifier CARD_HAND_ID = new Identifier(MOD_ID, "card_hand");
	public static final Item CARD_HAND = new CardHandItem(new Item.Settings());

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, CARD_HAND_ID, CARD_HAND);
	}
}