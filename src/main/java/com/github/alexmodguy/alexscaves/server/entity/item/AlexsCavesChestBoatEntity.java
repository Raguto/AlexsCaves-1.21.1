package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.AlexsCavesBoat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AlexsCavesChestBoatEntity extends ChestBoat implements AlexsCavesBoat {

    // Define our own data accessor for AC boat type
    private static final EntityDataAccessor<Integer> AC_BOAT_TYPE = SynchedEntityData.defineId(AlexsCavesChestBoatEntity.class, EntityDataSerializers.INT);
    
    // Track fall damage state locally
    private double acLastYd;

    public AlexsCavesChestBoatEntity(EntityType type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public AlexsCavesChestBoatEntity(Level level, double x, double y, double z) {
        this(ACEntityRegistry.CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public AlexsCavesChestBoatEntity(Level level, Vec3 location, AlexsCavesBoat.Type type) {
        this(level, location.x, location.y, location.z);
        this.setACBoatType(type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AC_BOAT_TYPE, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("ACBoatType", getACBoatType().getName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("ACBoatType")) {
            this.entityData.set(AC_BOAT_TYPE, AlexsCavesBoat.Type.byName(nbt.getString("ACBoatType")).ordinal());
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        this.acLastYd = this.getDeltaMovement().y;
        if (!this.isPassenger()) {
            if (onGround) {
                if (this.fallDistance > 3.0F) {
                    if (this.isInWater()) {
                        this.resetFallDistance();
                        return;
                    }

                    this.causeFallDamage(this.fallDistance, 1.0F, this.damageSources().fall());
                    if (!this.level().isClientSide && !this.isRemoved()) {
                        this.kill();
                        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            for (int i = 0; i < 3; ++i) {
                                this.spawnAtLocation(this.getACBoatType().getPlankSupplier().get());
                            }

                            for (int j = 0; j < 2; ++j) {
                                this.spawnAtLocation(Items.STICK);
                            }
                        }
                    }
                }

                this.resetFallDistance();
            } else if (!this.level().getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0.0D) {
                this.fallDistance -= (float) y;
            }
        }
    }

    public void setACBoatType(AlexsCavesBoat.Type type) {
        this.entityData.set(AC_BOAT_TYPE, type.ordinal());
    }

    public AlexsCavesBoat.Type getACBoatType() {
        return AlexsCavesBoat.Type.byId(this.entityData.get(AC_BOAT_TYPE));
    }

    @Override
    public void setVariant(Boat.Type vanillaType) {
    }

    @Override
    public Item getDropItem() {
        return getACBoatType().getChestDropSupplier().get();
    }

    @Override
    public Boat.Type getVariant() {
        return Boat.Type.OAK;
    }

}
