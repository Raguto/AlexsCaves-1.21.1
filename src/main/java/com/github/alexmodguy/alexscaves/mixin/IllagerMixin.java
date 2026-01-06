package com.github.alexmodguy.alexscaves.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractIllager.class)
public abstract class IllagerMixin {

    @Inject(
            method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = true
    )
    private void ac_isAlliedTo(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (isPossessed((Entity) (Object) this) || (other != null && isPossessed(other))) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isPossessed(Entity e) {
        return e.getPersistentData().getBoolean("TotemPossessed");
    }
}
