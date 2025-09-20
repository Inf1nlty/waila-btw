package cn.xylose.waila.mixin.accessor;

import net.minecraft.src.EntityZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityZombie.class)
public interface EntityZombieAccessor {

    @Accessor("conversionTime")
    int getConversionTime();
}