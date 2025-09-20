package cn.xylose.waila.mixin.accessor;

import net.minecraft.src.Entity;
import net.minecraft.src.Render;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Render.class)
public interface RenderAccessor {

    @Invoker("getEntityTexture")
    ResourceLocation getEntityTexture(Entity entity);
}