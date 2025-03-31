package mcp.mobius.waila;

import btw.AddonHandler;
import btw.BTWAddon;
import cn.xylose.waila.api.PacketDispatcher;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.client.ProxyClient;
import mcp.mobius.waila.commands.CommandDumpHandlers;
import mcp.mobius.waila.network.Packet0x00ServerPing;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.OverlayConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.*;
import net.minecraftforge.common.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Waila extends BTWAddon implements ModInitializer {
    public static String modsName = "Better Than Wolves";
    public static String modId = "waila";
    public static String modName = "Waila";

    public static Waila instance;
    public static Logger log = LogManager.getLogger(modName);
    public boolean serverPresent = false;
    private WailaPacketHandler wailaPacketHandler;
    public static ProxyClient proxy;

    @Override
    public void initialize() {
    }

    @Override
    public void onInitialize() {
    }

    public void loadWaila() {
        instance = new Waila();
        proxy = new ProxyClient();
        ConfigHandler.instance().config = new Configuration(new File(String.valueOf(FabricLoader.getInstance().getConfigDir()), "Waila.cfg"));
        DataAccessorCommon.instance = new DataAccessorCommon();
        proxy.registerHandlers();
        proxy.registerMods();
        proxy.registerIMCs();
        ConfigHandler.instance().loadDefaultConfig();
        OverlayConfig.updateColors();
    }

    @Override
    public void postSetup() {
        this.modID = modId;
        this.addonName = FabricLoader.getInstance().getModContainer(modID).get().getMetadata().getName();
        addResourcePackDomain(modId);
    }

    @Override
    public void registerAddonCommand(ICommand command) {
        AddonHandler.registerCommand(new CommandDumpHandlers(), false);
    }

    @Override
    public boolean serverCustomPacketReceived(NetServerHandler handler, Packet250CustomPayload packet) {
        if (this.wailaPacketHandler == null) {
            this.wailaPacketHandler = new WailaPacketHandler();
        }
        wailaPacketHandler.handleCustomPacket(handler, packet);
        return false;
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        PacketDispatcher.sendPacketToPlayer(Packet0x00ServerPing.create(), playerMP);
    }

    @Environment(EnvType.CLIENT)
    public boolean interceptCustomClientPacket(Minecraft mc, Packet250CustomPayload packet) {
        if (this.wailaPacketHandler == null) {
            this.wailaPacketHandler = new WailaPacketHandler();
        }
        this.wailaPacketHandler.handleCustomPacket(packet);
        return false;
    }
}
