package io.github.haykam821.lastcard.card.display;

import java.util.HashMap;
import java.util.Map;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.map.LastCardRegions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.TemplateRegion;

public abstract class CardDisplay {
	private final Map<Card, DrawableCanvas> canvasCache = new HashMap<>();
	private final VirtualDisplay display;

	protected CardDisplay(TemplateRegion region) {
		BlockBounds bounds = region.getBounds();
		BlockPos size = bounds.size();

		int rotation = region.getData() == null ? 0 : region.getData().getInt(LastCardRegions.ROTATION_KEY) % 4;

		int x = size.getX() + 1;
		int z = size.getZ() + 1;

		PlayerCanvas canvas = rotation % 2 == 0 ? DrawableCanvas.create(x, z) : DrawableCanvas.create(z, x);
		BlockPos pos = rotation == 1 || rotation == 2 ? bounds.max() : bounds.min();

		this.display = VirtualDisplay.of(canvas, pos, Direction.UP, rotation, false);
	}

	public void render() {
		CanvasUtils.clear(this.getCanvas());

		int x = CardSpacing.PADDING_X;
		int y = CardSpacing.PADDING_Y;

		int maxX = this.getCanvas().getWidth() - CardSpacing.PADDING_X;
		int maxHeight = 0;

		for (Card card : this.getCards()) {
			DrawableCanvas cardCanvas = this.getCardCanvas(card);

			if (cardCanvas != null) {
				int newX = x + cardCanvas.getWidth() + CardSpacing.GAP_X;
				maxHeight = Math.max(maxHeight, cardCanvas.getHeight());

				if (newX >= maxX) {
					x = CardSpacing.PADDING_X;
					y += maxHeight + CardSpacing.PADDING_Y;
				}

				CanvasUtils.draw(this.getCanvas(), x, y, cardCanvas);

				if (newX < maxX) {
					x = newX;
				}
			}
		}

		this.getCanvas().sendUpdates();
	}

	private final PlayerCanvas getCanvas() {
		return this.display.getCanvas();
	}

	public final void add(ServerPlayerEntity viewer) {
		this.display.addPlayer(viewer);
		this.getCanvas().addPlayer(viewer);
	}

	public final void remove(ServerPlayerEntity viewer) {
		this.display.removePlayer(viewer);
		this.getCanvas().removePlayer(viewer);
	}

	public final void destroy() {
		this.display.destroy();
		this.getCanvas().destroy();
	}

	public abstract Iterable<Card> getCards();

	public abstract DrawableCanvas renderCardCanvas(Card card);

	public final DrawableCanvas getCardCanvas(Card card) {
		return this.canvasCache.computeIfAbsent(card, this::renderCardCanvas);
	}
}
