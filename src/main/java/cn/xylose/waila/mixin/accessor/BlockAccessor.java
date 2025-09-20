package cn.xylose.waila.mixin.accessor;

import net.minecraft.src.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {

    @Invoker("getTextureName")
    String getTextureName();
}