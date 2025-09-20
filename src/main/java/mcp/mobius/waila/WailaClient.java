package mcp.mobius.waila;

import cn.xylose.waila.addons.btw.HUDHandlerBTWBlock;
import cn.xylose.waila.addons.btw.HUDHandlerBTWEntity;
import mcp.mobius.waila.client.ProxyClient;
import mcp.mobius.waila.overlay.OverlayConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.DefaultResourcePack;

@Environment(EnvType.CLIENT)
public class WailaClient implements ClientModInitializer {
    public static ProxyClient proxy;

    public void loadWailaClient() {
        proxy = new ProxyClient();
        proxy.registerHandlers();
        proxy.registerMods();
        proxy.registerIMCs();
        OverlayConfig.updateColors();
        HUDHandlerBTWBlock.register();
        HUDHandlerBTWEntity.register();
        DefaultResourcePack.defaultResourceDomains.add(Waila.modId);
    }

    @Override
    public void onInitializeClient() {
        OverlayConfig.updateColors();
    }
}
