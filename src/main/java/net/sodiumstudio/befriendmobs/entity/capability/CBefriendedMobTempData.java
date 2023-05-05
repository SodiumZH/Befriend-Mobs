package net.sodiumstudio.befriendmobs.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.registry.BefMobCapabilities;

/**
 * A temporal module for storage of data in IBefriendedMob interface.
 */
public interface CBefriendedMobTempData {

	public default Values values()
	{
		return (Values)this;
	}
	
	public class Values implements CBefriendedMobTempData
	{
		public IBefriendedMob mob;
		public Values(IBefriendedMob mob)
		{
			this.mob = mob;
			this.anchor = mob.asMob().position();
		}
		
		public boolean hasInit = false;
		public LivingEntity previousTarget = null;
		public Vec3 anchor;
	}
	
	public class Prvd implements ICapabilityProvider
	{
		
		public CBefriendedMobTempData values;
		
		public Prvd(IBefriendedMob mob)
		{
			values = new Values(mob);
		}
		
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == BefMobCapabilities.CAP_BEFRIENDED_MOB_TEMP_DATA)
				return LazyOptional.of(() -> {return this.values;}).cast();
			else return LazyOptional.empty();
		}
	}
}