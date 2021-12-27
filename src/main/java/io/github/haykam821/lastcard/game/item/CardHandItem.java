package io.github.haykam821.lastcard.game.item;

import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class CardHandItem extends Item implements PolymerItem {
	public CardHandItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, ServerPlayerEntity player) {
		return Items.KNOWLEDGE_BOOK;
	}
}
