package io.github.haykam821.lastcard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.haykam821.lastcard.Main;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;
import xyz.nucleoid.plasmid.game.manager.ManagedGameSpace;

@Mixin(WitherRoseBlock.class)
public class WitherRoseBlockMixin {
	@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z"))
	private boolean modifyWitherRoseInvulnerability(LivingEntity entity, DamageSource source) {
		if (entity instanceof ServerPlayerEntity player) {
			ManagedGameSpace gameSpace = GameSpaceManager.get().byPlayer(player);
			if (gameSpace != null && gameSpace.getBehavior().testRule(Main.WITHER_ROSE_WITHER_EFFECT) == ActionResult.FAIL) {
				return true;
			}
		}

		return entity.isInvulnerableTo(source);
	}
}
