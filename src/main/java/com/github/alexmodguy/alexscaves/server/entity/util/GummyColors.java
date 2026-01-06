package com.github.alexmodguy.alexscaves.server.entity.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public enum GummyColors {
    RED,
    GREEN,
    YELLOW,
    BLUE,
    PINK;

    public static final StreamCodec<ByteBuf, GummyColors> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
        GummyColors::fromOrdinal,
        GummyColors::ordinal
    );

    public static GummyColors fromOrdinal(int gummyColor) {
        return GummyColors.values()[Mth.clamp(gummyColor, 0, GummyColors.values().length - 1)];
    }

    public static GummyColors getRandom(RandomSource randomSource, boolean init){
        return  fromOrdinal(randomSource.nextInt(GummyColors.values().length - (init ? 0 : 1)));
    }
}
