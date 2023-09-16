package net.sodiumstudio.befriendmobs.entity.ai.goal.preset;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.sodiumstudio.befriendmobs.entity.ai.goal.BefriendedGoal;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;

// Adjusted from RangedAttackGoal
public abstract class BefriendedShootProjectileGoal extends BefriendedGoal {
	@Nullable
	protected LivingEntity target;
	protected int attackTime = -1;
	protected final double speedModifier;
	protected int seeTime;
	protected final int attackIntervalMin;
	protected final int attackIntervalMax;
	protected final float attackRadius;
	protected final float attackRadiusSqr;

	public BefriendedShootProjectileGoal(IBefriendedMob mob, double pSpeedModifier, int pAttackInterval,
			float pAttackRadius) {
		this(mob, pSpeedModifier, pAttackInterval, pAttackInterval, pAttackRadius);
	}

	public BefriendedShootProjectileGoal(IBefriendedMob mob, double pSpeedModifier, int pAttackIntervalMin,
			int pAttackIntervalMax, float pAttackRadius) {
		super(mob);
		this.mob = mob;
		this.speedModifier = pSpeedModifier;
		this.attackIntervalMin = pAttackIntervalMin;
		this.attackIntervalMax = pAttackIntervalMax;
		this.attackRadius = pAttackRadius;
		this.attackRadiusSqr = pAttackRadius * pAttackRadius;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		allowAllStatesExceptWait();
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state
	 * necessary for execution in this method as well.
	 */
	@Override
	public boolean checkCanUse() {
		if (isDisabled())
			return false;
		LivingEntity livingentity = this.updateTarget();
		if (livingentity != null && livingentity.isAlive()) {
			this.target = livingentity;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean checkCanContinueToUse() {
		return this.checkCanUse() || !this.mob.asMob().getNavigation().isDone();
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	@Override
	public void stop() {
		this.target = null;
		this.seeTime = 0;
		this.attackTime = -1;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void tick() {
		double d0 = this.mob.asMob().distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
		boolean flag = this.mob.asMob().getSensing().hasLineOfSight(this.target);
		if (flag) {
			++this.seeTime;
		} else {
			this.seeTime = 0;
		}

		if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
			this.mob.asMob().getNavigation().stop();
		} else {
			this.mob.asMob().getNavigation().moveTo(this.target, this.speedModifier);
		}

		this.mob.asMob().getLookControl().setLookAt(this.target, 30.0F, 30.0F);
		if (--this.attackTime == 0) {
			if (!flag) {
				return;
			}

			float f = (float) Math.sqrt(d0) / this.attackRadius;
			float f1 = Mth.clamp(f, 0.1F, 1.0F);
			this.performShooting(this.target, f1);	// Removed RangedAttackMob 
			this.attackTime = Mth.floor(
					f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
		} else if (this.attackTime < 0) {
			this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius,
					(double) this.attackIntervalMin, (double) this.attackIntervalMax));
		}
	}
	
	/**
	 * Method to perform shooting.
	 */
	protected abstract void performShooting(LivingEntity target, float velocity);
	
	/**
	 * Method to update target.
	 * @return New target to set.
	 */
	protected abstract LivingEntity updateTarget();
}