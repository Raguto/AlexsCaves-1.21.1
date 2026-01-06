package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.server.block.SirenLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SirenLightBlockEntity extends BlockEntity {
    private float onProgress;
    private float prevOnProgress;

    private float sirenRotation;
    private float prevSirenRotation;

    private int color = -1;

    public SirenLightBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.SIREN_LIGHT.get(), pos, state);
        if (state.getValue(SirenLightBlock.POWERED)) {
            prevOnProgress = onProgress = 10.0F;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, SirenLightBlockEntity entity) {
        entity.prevOnProgress = entity.onProgress;
        entity.prevSirenRotation = entity.sirenRotation;
        boolean powered = state.getValue(SirenLightBlock.POWERED);

        if (powered && entity.onProgress < 10.0F) {
            entity.onProgress += 1F;
        } else if (!powered && entity.onProgress > 0.0F) {
            entity.onProgress -= 1F;
        }
        if (powered) {
            entity.sirenRotation += entity.onProgress * 2F + 0.25F;
        }
    }

    public float getOnProgress(float partialTicks) {
        return (prevOnProgress + (onProgress - prevOnProgress) * partialTicks) * 0.1F;
    }

    public float getSirenRotation(float partialTicks) {
        return (prevSirenRotation + (sirenRotation - prevSirenRotation) * partialTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.color = tag.getInt("Color");
    }

    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Color", this.color);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, net.minecraft.core.HolderLookup.Provider registries) {
        if (packet != null && packet.getTag() != null) {
            this.color = packet.getTag().getInt("Color");
        }
    }


    public boolean setColor(int setTo) {
        if (this.color == setTo) return false;
        this.color = setTo;
        this.setChanged();
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        return true;
    }

    public int getColor() {
        return color < 0 ? 0X00FF00 : color;
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        return new AABB(Vec3.atLowerCornerOf(pos.offset(-3, -3, -3)), Vec3.atLowerCornerOf(pos.offset(4, 4, 4)));
    }

    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

}
