package net.sodiumstudio.befriendmobs.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.befriending.registry.BefriendableMobRegistry;
import net.sodiumstudio.befriendmobs.entity.befriending.registry.BefriendingTypeRegistry;
import net.sodiumstudio.befriendmobs.entity.capability.CAttributeMonitorProvider;
import net.sodiumstudio.befriendmobs.entity.capability.CBefriendableMobProvider;
import net.sodiumstudio.befriendmobs.entity.capability.CHealingHandlerProvider;
import net.sodiumstudio.befriendmobs.entity.capability.LivingSetupAttributeMonitorEvent;
import net.sodiumstudio.befriendmobs.item.ItemMobRespawner;
import net.sodiumstudio.befriendmobs.item.baublesystem.CBaubleDataCache;
import net.sodiumstudio.befriendmobs.item.baublesystem.IBaubleHolder;
import net.sodiumstudio.befriendmobs.item.capability.CMobRespawnerProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BefMobCapabilityAttachment {

	// Attach capabilities
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void attachLivingEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		// Attribute change monitor
		if (event.getObject() instanceof LivingEntity living)
		{
			CAttributeMonitorProvider prvd = new CAttributeMonitorProvider(living);
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID)
					, prvd);
			MinecraftForge.EVENT_BUS.post(new LivingSetupAttributeMonitorEvent(living, prvd));
		}			
		// CBefriendableMob
		if (event.getObject() instanceof Mob mob) {
			if (BefriendingTypeRegistry.contains((EntityType<? extends Mob>) mob.getType())
					&& !(mob instanceof IBefriendedMob)) 
			{
				event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_befriendable"),
						new CBefriendableMobProvider(mob));
				if (BefriendingTypeRegistry.contains((EntityType<? extends Mob>) mob.getType()) 
						&& BefriendingTypeRegistry.getHandler((EntityType<? extends Mob>) mob.getType()) != null)
				{
					// Initialize capability (defined in handlers)
					mob.getCapability(BefMobCapabilities.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> 
					{
						BefriendingTypeRegistry.getHandler((EntityType<? extends Mob>) mob.getType()).initCap(l);
						BefriendableMobRegistry.put(mob);
					});
				}
			}
		}
		// CHealingHandler
		if (event.getObject() instanceof IBefriendedMob bef)
		{
			if (bef.healingHandlerClass() != null)
			{
				try
				{
					event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_healing_handler"), 
						new CHealingHandlerProvider(
							// Implementation class defined in IBefriendedMob implementation
							bef.healingHandlerClass().getDeclaredConstructor(LivingEntity.class).newInstance(bef.asMob()), 
							bef.asMob()));
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		// CBaubleDataCache
		if (event.getObject() instanceof IBaubleHolder b)
		{
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_bauble_data_cache"), 
					new CBaubleDataCache.Prvd(b));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
	{
		ItemStack stack = event.getObject();
		if (stack.getItem() != null && stack.getItem() instanceof ItemMobRespawner)
		{
			event.addCapability(new ResourceLocation(BefriendMobs.MOD_ID, "cap_befriended_respwner"),
					new CMobRespawnerProvider(stack));
		}
	}
}