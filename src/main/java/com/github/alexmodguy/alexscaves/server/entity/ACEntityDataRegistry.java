package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

public class ACEntityDataRegistry {

    public static final DeferredRegister<EntityDataSerializer<?>> DEF_REG = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AlexsCaves.MODID);
    
    private static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, Vec3::x,
        ByteBufCodecs.DOUBLE, Vec3::y,
        ByteBufCodecs.DOUBLE, Vec3::z,
        Vec3::new
    );
    
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<Vec3>>> OPTIONAL_VEC_3 = DEF_REG.register("optional_vec_3", () -> EntityDataSerializer.forValueType(ByteBufCodecs.optional(VEC3_STREAM_CODEC)));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<GummyColors>> GUMMY_COLOR = DEF_REG.register("gummy_color", () -> EntityDataSerializer.forValueType(GummyColors.STREAM_CODEC));

}
