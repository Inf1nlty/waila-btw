package cn.xylose.waila.mixin;

import mcp.mobius.waila.Waila;
import net.minecraft.src.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "startServer", at = @At(value = "RETURN", ordinal = 1))
    private void onStartServer(CallbackInfoReturnable<Boolean> cir) {
        Waila wailaAddon = new Waila();
        wailaAddon.loadWaila();
    }
}
