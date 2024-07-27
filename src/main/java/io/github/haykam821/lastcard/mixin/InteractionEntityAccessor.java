package io.github.haykam821.lastcard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.decoration.InteractionEntity;

@Mixin(InteractionEntity.class)
public interface InteractionEntityAccessor {
	@Invoker("setInteractionWidth")
	public void lastcard$setInteractionWidth(float width);

	@Invoker("setInteractionHeight")
	public void lastcard$setInteractionHeight(float height);
}
