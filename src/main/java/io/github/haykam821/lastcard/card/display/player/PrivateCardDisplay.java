package io.github.haykam821.lastcard.card.display.player;

import java.util.List;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardRenderData;
import io.github.haykam821.lastcard.card.display.region.CardRegion;
import io.github.haykam821.lastcard.card.display.region.OpenSelectorCardRegion;
import io.github.haykam821.lastcard.card.display.region.PlayCardRegion;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PrivateCardDisplay extends PlayerCardDisplay {
	private Card selectingCard = null;

	public PrivateCardDisplay(AbstractPlayerEntry player, TemplateRegion region) {
		super(player, region);
	}

	public void setSelectingCard(Card card) {
		this.selectingCard = card;
		this.update();
	}

	@Override
	public Iterable<Card> getCards() {
		if (this.selectingCard != null) {
			return List.of(this.selectingCard);
		}

		return super.getCards();
	}

	@Override
	public boolean shouldFlattenSelectors() {
		return this.selectingCard != null;
	}

	@Override
	public DrawableCanvas renderCardCanvas(CardRenderData data) {
		return data.renderCard();
	}

	@Override
	public boolean hasOutline(Card card) {
		return card.canPlay(this.player);
	}

	@Override
	public CardRegion getCardRegion(Card card, CardColor overrideColor, int minX, int minY, int maxX, int maxY) {
		if (card.getSelector().getSelectableColors() != null && this.selectingCard == null) {
			return new OpenSelectorCardRegion(card, this, minX, minY, maxX, maxY);
		}

		return new PlayCardRegion(card, overrideColor, this, minX, minY, maxX, maxY);
	}
}
