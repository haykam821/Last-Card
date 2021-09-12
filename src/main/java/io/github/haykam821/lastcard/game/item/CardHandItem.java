package io.github.haykam821.lastcard.game.item;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class CardHandItem extends Item implements VirtualItem {
	public CardHandItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public Item getVirtualItem() {
		return Items.KNOWLEDGE_BOOK;
	}
}
