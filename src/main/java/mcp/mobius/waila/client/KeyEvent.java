package mcp.mobius.waila.client;

import cn.xylose.waila.handlers.emi.EMIHandler;
import emi.dev.emi.emi.api.EmiApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Minecraft;
import net.minecraftforge.common.Configuration;
import org.lwjgl.input.Keyboard;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import mcp.mobius.waila.utils.Constants;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class KeyEvent {
    public static KeyEvent instance;
    public KeyBinding key_cfg;
    public KeyBinding key_show;
    public KeyBinding key_liquid;
    public KeyBinding key_recipe;
    public KeyBinding key_usage;
    public KeyBinding key_show_advanced;
    Minecraft mc = Minecraft.getMinecraft();

    public KeyEvent() {
        instance = this;
        key_cfg = new KeyBinding(Constants.BIND_WAILA_CFG, Keyboard.KEY_NUMPAD0);
        key_show = new KeyBinding(Constants.BIND_WAILA_SHOW, Keyboard.KEY_NUMPAD1);
        key_liquid = new KeyBinding(Constants.BIND_WAILA_LIQUID, Keyboard.KEY_NUMPAD2);
        key_recipe = new KeyBinding(Constants.BIND_WAILA_RECIPE, Keyboard.KEY_NUMPAD3);
        key_usage = new KeyBinding(Constants.BIND_WAILA_USAGE, Keyboard.KEY_NUMPAD4);
        key_show_advanced = new KeyBinding(Constants.BIND_WAILA_SHOW_ADVANCED, Keyboard.KEY_LMENU);
    }

    public void onKeyEvent() {
        boolean showKey = key_show.isPressed();
        if (key_cfg.isPressed()) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new ScreenConfig(null));
            }
            return;
        }
        if (showKey && ConfigHandler.instance()
                .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)) {
            boolean status = ConfigHandler.instance()
                    .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, !status);
        } else if (showKey && !ConfigHandler.instance()
                .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)) {
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
        } else if (key_liquid.isPressed()) {
            boolean status = ConfigHandler.instance()
                    .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true);
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, !status);
        } else if (key_recipe.isPressed()) {
            EMIHandler.displayRecipes();
        } else if (key_usage.isPressed()) {
            EMIHandler.displayUses();
        }
    }

}
