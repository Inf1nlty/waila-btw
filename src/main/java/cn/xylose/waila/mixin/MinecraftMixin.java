package cn.xylose.waila.mixin;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.client.KeyEvent;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Unique private Waila wailaAddon;

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onKeyEvent(CallbackInfo ci) {
        KeyEvent.instance.onKeyEvent();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void onStartGame(CallbackInfo ci) {
        if (this.wailaAddon == null) {
            this.wailaAddon = new Waila();
            this.wailaAddon.loadWaila();
        }
    }
}
