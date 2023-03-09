package net.sodiumstudio.befriendmobs.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.entitiy.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entitiy.befriending.BefriendableMobInteractArguments;
import net.sodiumstudio.befriendmobs.entitiy.befriending.BefriendableMobInteractionResult;
import net.sodiumstudio.befriendmobs.entitiy.befriending.registry.BefriendingTypeRegistry;
import net.sodiumstudio.befriendmobs.event.events.BefriendableMobInteractEvent;
import net.sodiumstudio.befriendmobs.event.events.MobBefriendEvent;
import net.sodiumstudio.befriendmobs.registry.RegCapabilities;
import net.sodiumstudio.befriendmobs.registry.RegItems;
import net.sodiumstudio.befriendmobs.util.Debug;
import net.sodiumstudio.befriendmobs.util.EntityHelper;
import net.sodiumstudio.befriendmobs.util.Util;
import net.sodiumstudio.befriendmobs.util.exceptions.UnimplementedException;

// TODO: change modid after isolation
@Mod.EventBusSubscriber(modid = BefriendMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents
{

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		Entity target = event.getTarget();
		Player player = event.getPlayer();
		// Wrap result as an array to set in lambda
		InteractionResult[] result = { InteractionResult.PASS };
		boolean isClientSide = event.getSide() == LogicalSide.CLIENT;
		boolean isMainHand = event.getHand() == InteractionHand.MAIN_HAND;
		Util.GlobalBoolean shouldPostInteractEvent = Util.createGB(false);

		// Mob interaction start //
		if (target != null && target instanceof LivingEntity) 
		{
			LivingEntity living = (LivingEntity) target;
			// Handle befriendable mob start //
			if (living.getCapability(RegCapabilities.CAP_BEFRIENDABLE_MOB).isPresent()
					&& !(living instanceof IBefriendedMob)) {
				living.getCapability(RegCapabilities.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> 
				{
					/* For debug befriender only */
					if (player.getMainHandItem().getItem() == RegItems.DEBUG_BEFRIENDER.get()) 
					{
						if (!isClientSide && isMainHand) 
						{
							IBefriendedMob bef = BefriendingTypeRegistry.getHandler(living.getType()).befriend(player,
									living);
							if (bef != null) 
							{
								MinecraftForge.EVENT_BUS.post(new MobBefriendEvent(player, living, bef));
								EntityHelper.sendHeartParticlesToMob(bef.asLiving()); // TODO: move this to a MobBefriendEvent listener
								event.setCancellationResult(InteractionResult.sidedSuccess(isClientSide));
								return;
							} 
							else throw new UnimplementedException("Entity type befriend method unimplemented: "
										+ living.getType().toShortString() + ", handler class: "
										+ BefriendingTypeRegistry.getHandler(living.getType()).toString());
						}
						MinecraftForge.EVENT_BUS.post(
								new BefriendableMobInteractEvent(event.getSide(), player, living, event.getHand()));
						result[0] = InteractionResult.sidedSuccess(isClientSide);
					}

					/* Main actions */
					else 
					{
						BefriendableMobInteractionResult res = BefriendingTypeRegistry.getHandler(living.getType())
								.handleInteract(BefriendableMobInteractArguments.of(event.getSide(), player, living,
										event.getHand()));
						if (res.befriendedMob != null) // Directly exit if befriended, as this mob is no longer valid
						{
							event.setCanceled(true);
							event.setCancellationResult(InteractionResult.sidedSuccess(isClientSide));
							return;
						} 
						else if (res.handled) 
						{
							event.setCanceled(true);
							result[0] = InteractionResult.sidedSuccess(isClientSide);
							shouldPostInteractEvent.set(true);
							MinecraftForge.EVENT_BUS.post(
									new BefriendableMobInteractEvent(event.getSide(), player, living, event.getHand()));
						}
					}
				});
			}
			// Handle befriendable mob end //
			// Handle befriended mob start //
			else if (living instanceof IBefriendedMob bef) 
			{
				// if (!isClientSide && isMainHand)

				if (player.isShiftKeyDown() && player.getMainHandItem().getItem() == RegItems.DEBUG_BEFRIENDER.get()) {
					bef.init(player.getUUID(), null);
					// Debug.printToScreen("Befriended mob initialized", player, living);
					result[0] = InteractionResult.sidedSuccess(isClientSide);
				}
				else 
				{
					result[0] = (player.isShiftKeyDown() ? bef.onInteractionShift(player, event.getHand())
							: bef.onInteraction(player, event.getHand())) ? InteractionResult.sidedSuccess(isClientSide)
									: result[0];
				}
			}
			// Handle befriended mob end //
		}
		// Mob interaction end //

		// Server events end //
		// Client events start //
		else {
		}
		// Client events end //
		event.setCanceled(result[0] == InteractionResult.sidedSuccess(isClientSide));
		event.setCancellationResult(result[0]);
	}
	
	
	@SubscribeEvent
	public static void onLivingSetAttackTargetEvent(LivingSetAttackTargetEvent event) {
		LivingEntity lastHurtBy = event.getEntityLiving().getLastHurtByMob();
		LivingEntity target = event.getTarget();
		Util.GlobalBoolean isCancelledByEffect = Util.createGB(false);

		// Handle mobs //
		if (target != null && event.getEntity() instanceof Mob mob)
		{
			// Handle befriendable mobs //
			if (target instanceof Player player && mob.getCapability(RegCapabilities.CAP_BEFRIENDABLE_MOB).isPresent())
			{
				mob.getCapability(RegCapabilities.CAP_BEFRIENDABLE_MOB).ifPresent((l) ->
				{
					// Add to hatred list (disable befriending permanently)
					if (target != null && !l.getHatred().contains(player.getUUID()) && !isCancelledByEffect.get())
					{
						l.addHatred(player);
						Debug.printToScreen("Player " + Util.getNameString(player) + " put into hatred list by "
								+ Util.getNameString(mob), player, player);
					}
				});
			}
			// Handle befriendable mobs end //
			if (mob instanceof IBefriendedMob bef)
			{
				// Befriended mob should never attack the owner
				if (target == bef.getOwner())
					mob.setTarget(bef.getPreviousTarget());
				else
					bef.setPreviousTarget(target);
			}
		}
		// Handle mobs end //
	}	
}
