package net.sodiumstudio.befriendmobs.entity.ai.goal.preset;

import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.ai.goal.BefriendedGoal;

/* Block the actions below under certain conditions */
public abstract class BefriendedBlockActionGoal extends BefriendedGoal
{
	
	public BefriendedBlockActionGoal(IBefriendedMob mob)
	{
		super(mob);
	}
	
	@Override
	public boolean canUse()
	{
		return blockCondition();
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return blockCondition();
	}
	
	@Override
	public void start()
	{
		mob.asMob().getNavigation().stop();
	}
	
	public abstract boolean blockCondition();
}
