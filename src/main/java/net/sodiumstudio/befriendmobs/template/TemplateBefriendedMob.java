package net.sodiumstudio.befriendmobs.template;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.entity.befriended.BefriendedHelper;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryWithEquipment;
import net.sodiumstudio.befriendmobs.item.MobRespawnerItem;
import net.sodiumstudio.nautils.exceptions.UnimplementedException;

/** This is a template for befriended mob class.
* You may copy-paste the code below to your class and modify at labeled places.
* For more preset see {@code TemplateBefriendedMobPreset}.
*/
public class TemplateBefriendedMob /* Your mob class */ extends PathfinderMob /* Your mob superclass */ implements IBefriendedMob
{
	
	// Data sync 

	// By default owner uuid and ai state need to sync
	// It's recommended to always keep these two values as synched data
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID = SynchedEntityData
			.defineId(TemplateBefriendedMob.class/* This class */, EntityDataSerializers.OPTIONAL_UUID);
	protected static final EntityDataAccessor<Integer> DATA_AISTATE = SynchedEntityData
			.defineId(TemplateBefriendedMob.class/* This class */, EntityDataSerializers.INT);
	/* More data to sync... */

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DATA_OWNERUUID, Optional.empty());
		entityData.define(DATA_AISTATE, 0);
		/* More data to sync */
	}

	@Override
	public EntityDataAccessor<Optional<UUID>> getOwnerUUIDAccessor() {
		return DATA_OWNERUUID;
	}

	@Override
	public EntityDataAccessor<Integer> getAIStateData() {
		return DATA_AISTATE;
	}


	// Initialization
	
	public TemplateBefriendedMob/* Your mob class */ (EntityType<? extends PathfinderMob /* Your mob class */ > pEntityType, Level pLevel) {
		super(pEntityType, pLevel);	
		this.xpReward = 0;
		Arrays.fill(this.armorDropChances, 0f);
		Arrays.fill(this.handDropChances, 0f);
		/* Initialization */
	}

	public static Builder createAttributes() {
		return null; /* Change to your attribute */
	}

	@Override
	protected void registerGoals() {
		/* Add register goals */
	}

	// Initialization end

	// Interaction
	
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		/* Do something... */
		return InteractionResult.PASS;	/* Change to the real result */
	}


	// Interaction end
	
	// Inventory related
	// Generally no need to modify unless noted
	
	protected BefriendedInventory befriendedInventory = new BefriendedInventory(getInventorySize());

	@Override
	public BefriendedInventory getAdditionalInventory()
	{
		return befriendedInventory;
	}

	@Override
	public int getInventorySize()
	{
		/* Change to your size */
		return 0;
	}

	@Override
	public void updateFromInventory() {
		if (!this.level.isClientSide) {
			/* If mob's properties (e.g. equipment, HP, etc.) needs to sync with inventory, set here */
		}
	}

	@Override
	public void setInventoryFromMob() {
		if (!this.level.isClientSide) {
			/* If inventory needs to be set from mob's properties on initialization, set here */
		}
	}

	@Override
	public BefriendedInventoryMenu makeMenu(int containerId, Inventory playerInventory, Container container) {
		return null; /* return new YourMenuClass(containerId, playerInventory, container, this) */
	}
	
	// Inventory end

	// save&load

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		BefriendedHelper.addBefriendedCommonSaveData(this, nbt);
		/* Add more save data... */
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		BefriendedHelper.readBefriendedCommonSaveData(this, nbt);
		/* Add more save data... */
		this.setInit();
	}


	
	// Data sync end 
	
	// Misc

	@Override
	public String getModId() {
		/* Return your mod id */
		throw new UnimplementedException("Missing Mod ID");
	}

	@Override
	public MobRespawnerItem getRespawnerType() {
		/* If needed, set to your respawner type. Leave it null if you don't need respawner. */
		return null;
	}
	
	
	// ==================================================================== //
	// ========================= General Settings ========================= //
	// Generally these can be copy-pasted to other IBefriendedMob classes //

	@Override
	public boolean isPersistenceRequired() {
		return true;
	}

	/* add @Override annotation if inheriting Monster class */
	/* @Override */
	public boolean isPreventingPlayerRest(Player pPlayer) {
		return false;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}


	// ========================= General Settings end ========================= //
	// ======================================================================== //

}
