package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MovingMetalBlockEntity extends AbstractMovingBlockEntity {

    public MovingMetalBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

@Override
    public boolean movesEntities() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

}
