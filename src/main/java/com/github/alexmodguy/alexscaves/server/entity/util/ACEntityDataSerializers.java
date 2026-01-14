package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;

public class ACEntityDataSerializers {
    public static final EntityDataAccessor<Float> MAGNET_DELTA_X = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> MAGNET_DELTA_Y = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> MAGNET_DELTA_Z = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Direction> MAGNET_ATTACHMENT_DIRECTION = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.DIRECTION);
}
