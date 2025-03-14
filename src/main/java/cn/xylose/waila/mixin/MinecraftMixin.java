package cn.xylose.waila.mixin;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.client.KeyEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.Minecraft;
import net.minecraftforge.common.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Unique private KeyEvent key;
    @Unique private Waila wailaAddon;

    @Inject(method = "startGame", at = @At("TAIL"))
    private void registerKeybindings(CallbackInfo ci) {
        key = new KeyEvent();
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void keyEvent(CallbackInfo ci) {
        key.onKeyEvent();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void onWorldUnload(CallbackInfo ci) {
        if (this.wailaAddon == null) {
            ConfigHandler.instance().config = new Configuration(new File(String.valueOf(FabricLoader.getInstance().getConfigDir()), "waila.cfg"));
            this.wailaAddon = new Waila();
            this.wailaAddon.load();
        }
    }
}
