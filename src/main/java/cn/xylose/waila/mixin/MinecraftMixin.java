package cn.xylose.waila.mixin;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.client.KeyEvent;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "runTick", at = @At("TAIL"))
    private void wailaKeyEvent(CallbackInfo ci) {
        KeyEvent.instance.onKeyEvent();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void initWailaAddon(CallbackInfo ci) {
        Waila wailaAddon = new Waila();
        wailaAddon.loadWaila();
        WailaClient wailaClient = new WailaClient();
        wailaClient.loadWailaClient();
    }
}
