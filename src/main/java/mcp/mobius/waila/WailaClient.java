package mcp.mobius.waila;

import btw.BTWAddon;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.client.ProxyClient;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.OverlayConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.Configuration;

import java.io.File;

@Environment(EnvType.CLIENT)
public class WailaClient extends BTWAddon implements ClientModInitializer {
    public static ProxyClient proxy;

    public void loadWailaClient() {
        proxy = new ProxyClient();
        proxy.registerHandlers();
        proxy.registerMods();
        proxy.registerIMCs();
        OverlayConfig.updateColors();
    }

    @Override
    public void initialize() {
        OverlayConfig.updateColors();
    }

    @Override
    public void onInitializeClient() {
    }

    @Override
    public void postSetup() {
        addResourcePackDomain(Waila.modId);
    }
}
