package cn.xylose.waila.mixin;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.src.Minecraft;
import net.minecraftforge.common.Configuration;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "runTick", at = @At("TAIL"))
    private void wailaKeyEvent(CallbackInfo ci) {
        KeyEvent.instance.onKeyEvent();
        if (!Keyboard.isKeyDown(KeyEvent.instance.key_show.keyCode)
                && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)
                && ConfigHandler.instance()
                .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false)) {
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);
        }
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void initWailaAddon(CallbackInfo ci) {
        Waila wailaAddon = new Waila();
        wailaAddon.loadWaila();
        WailaClient wailaClient = new WailaClient();
        wailaClient.loadWailaClient();
    }
}
