package io.github.haykam821.lastcard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.decoration.ArmorStandEntity;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
	@Invoker("setMarker")
	public void lastcard$setMarker(boolean marker);
}
