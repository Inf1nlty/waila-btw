package cn.xylose.waila;

import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import net.minecraft.src.GuiOptions;
import net.minecraft.src.Minecraft;

import java.util.Map;

public class WailaModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ScreenConfig::new;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of("minecraft", parent -> new GuiOptions(parent, Minecraft.getMinecraft().gameSettings, true));
    }
}
