package cn.xylose.waila.mixin.accessor;

import net.minecraft.src.EntityChicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityChicken.class)

public interface EntityChickenAccessor {

    @Accessor("timeToLayEgg")
    long getTimeToLayEgg();
}