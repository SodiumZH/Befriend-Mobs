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
import net.sodiumstudio.befriendmobs.entity.BefriendedHelper;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.entity.ai.BefriendedAIState;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventory;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryWithEquipment;
import net.sodiumstudio.befriendmobs.util.exceptions.UnimplementedException;

// This is a template for befriended mob class.
// You may copy-paste the code below to your class and modify at places labeled by /*...*/.
public class TemplateBefriendedMob /* Your mob class */ extends PathfinderMob /* Your mob superclass */ implements IBefriendedMob
{
	
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
	
	BefriendedInventory befriendedInventory = new BefriendedInventoryWithEquipment(getInventorySize());

	@Override
	public BefriendedInventory getAdditionalInventory()
	{
		return befriendedInventory;
	}

	@Override
	public int getInventorySize()
	{
		/* Change to your size */
		return 8;
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
		// Change modid to your modid
		BefriendedHelper.addBefriendedCommonSaveData(this, nbt, BefriendMobs.MOD_ID);
		/* Add more save data... */
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		// Change modid to your modid
		BefriendedHelper.readBefriendedCommonSaveData(this, nbt, BefriendMobs.MOD_ID);
		/* Add more save data... */
		this.setInit();
	}

	// Data sync 

	// By default owner uuid and ai state need to sync
	// It's recommended to always keep these two values as synched data
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID = SynchedEntityData
			.defineId(TemplateBefriendedMob.class/* This class */, EntityDataSerializers.OPTIONAL_UUID);
	protected static final EntityDataAccessor<Byte> DATA_AISTATE = SynchedEntityData
			.defineId(TemplateBefriendedMob.class/* This class */, EntityDataSerializers.BYTE);
	/* More data to sync... */

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DATA_OWNERUUID, Optional.empty());
		entityData.define(DATA_AISTATE, (byte) 0);
		/* More data to sync */
	}

	// Data sync end 
	
	// Misc

	@Override
	public String getModId() {
		throw new UnimplementedException("Missing Mod ID");
	}


	
	
	// ==================================================================== //
	// ========================= General Settings ========================= //
	// Generally these can be copy-pasted to other IBefriendedMob classes //

	// ------------------ IBefriendedMob interface ------------------ //

	protected boolean initialized = false;
	
	@Override
	public boolean hasInit()
	{
		return initialized;
	}
	
	@Override
	public void setInit()
	{
		initialized = true;
	}
		
	@Override
	public Player getOwner() {
		return getOwnerUUID() != null ? level.getPlayerByUUID(getOwnerUUID()) : null;
	}

	@Override
	public void setOwner(Player owner) {
		entityData.set(DATA_OWNERUUID, Optional.of(owner.getUUID()));
	}

	@Override
	public UUID getOwnerUUID() {
		return entityData.get(DATA_OWNERUUID).orElse(null);
	}

	@Override
	public void setOwnerUUID(UUID ownerUUID) {
		entityData.set(DATA_OWNERUUID, Optional.of(ownerUUID));
	}

	// AI related
	
	@Override
	public BefriendedAIState getAIState() {
		return BefriendedAIState.fromID(entityData.get(DATA_AISTATE));
	}

	@Override
	public void setAIState(BefriendedAIState state) {
		entityData.set(DATA_AISTATE, state.id());
	}

	protected LivingEntity PreviousTarget = null;

	@Override
	public LivingEntity getPreviousTarget() {
		return PreviousTarget;
	}

	@Override
	public void setPreviousTarget(LivingEntity target) {
		PreviousTarget = target;
	}
	
	/* Inventory */

	// ------------------ IBefriendedMob interface end ------------------ //

	// ------------------ Misc ------------------ //

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