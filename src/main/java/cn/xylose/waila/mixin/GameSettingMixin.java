package cn.xylose.waila.mixin;

import mcp.mobius.waila.client.KeyEvent;
import net.minecraft.src.GameSettings;
import net.minecraft.src.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameSettings.class)
public class GameSettingMixin {

    @Shadow public KeyBinding[] keyBindings;

    @Inject(method = "<init>(Lnet/minecraft/src/Minecraft;Ljava/io/File;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GameSettings;mc:Lnet/minecraft/src/Minecraft;"))
    public void initWailaKeybindings(CallbackInfo ci) {
        new KeyEvent();
        List<KeyBinding> list = new ArrayList<>();
        list.add(KeyEvent.instance.key_cfg);
        list.add(KeyEvent.instance.key_show);
        list.add(KeyEvent.instance.key_liquid);
        list.add(KeyEvent.instance.key_recipe);
        list.add(KeyEvent.instance.key_usage);
        this.keyBindings = ArrayUtils.addAll(this.keyBindings, list.toArray(KeyBinding[]::new));
    }
}
