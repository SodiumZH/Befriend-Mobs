package net.sodiumstudio.befriendmobs.entity.befriending.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.sodiumstudio.befriendmobs.entity.befriending.BefriendingHandler;

public class BefriendingTypeRegisterEvent extends Event implements IModBusEvent {
	
	public void register(EntityType<? extends Mob> befriendable, EntityType<? extends Mob> convertTo, BefriendingHandler handler, boolean force)
	{
		BefriendingTypeRegistry.register(befriendable, convertTo, handler, force);
	}
	
	public void register(EntityType<? extends Mob> befriendable, EntityType<? extends Mob> convertTo, BefriendingHandler handler)
	{
		BefriendingTypeRegistry.register(befriendable, convertTo, handler);
	}
	
}