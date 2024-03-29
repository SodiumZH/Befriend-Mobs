package net.sodiumstudio.nautils.mixins.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumstudio.nautils.events.LivingEntitySweepHurtEvent;
import net.sodiumstudio.nautils.mixins.NaUtilsMixin;

@Mixin(Player.class)
public class NaUtilsMixinPlayer implements NaUtilsMixin<Player>
{

	// This is the last condition of sweeping, so canceling this means canceling sweeping	
	@WrapOperation(method = "attack(Lnet/minecraft/world/entity/Entity;)V",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;canHit(Lnet/minecraft/world/entity/Entity;D)Z"))
	private boolean acceptSweepDamage(Player caller, Entity entity, double amount, Operation<Boolean> original)
	{
		// If originally true, all sweeping conditions are satisfied, so post event and check if cancelled
		if (entity instanceof LivingEntity living && MinecraftForge.EVENT_BUS.post(new LivingEntitySweepHurtEvent(living, this.get())))
			return false;
		// If originally false, it shouldn't take sweep hurt at all, so no posting and return false
		else return original.call(caller, entity, amount);
	}
}
