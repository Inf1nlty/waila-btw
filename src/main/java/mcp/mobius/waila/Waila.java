package mcp.mobius.waila;

import btw.BTWAddon;
import btw.BTWMod;
import btw.client.network.packet.handler.*;
import cn.xylose.waila.api.PacketDispatcher;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.client.ProxyClient;
import mcp.mobius.waila.network.Packet0x00ServerPing;
import mcp.mobius.waila.network.Packet0x01TERequest;
import mcp.mobius.waila.network.Packet0x02TENBTData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Minecraft;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet250CustomPayload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.OverlayConfig;

public class Waila extends BTWAddon implements ModInitializer {
    public static String modsName = "Better Than Wolves";
    public static String modId = "waila";
    public static String modName = "Waila";

    public static Waila instance;
    public static Logger log = LogManager.getLogger(modName);
    public boolean serverPresent = false;
    private WailaPacketHandler wailaPacketHandler;
    public static ProxyClient proxy;

    public void load() {
        instance = new Waila();
        proxy = new ProxyClient();
        proxy.registerHandlers();
        proxy.registerMods();
        proxy.registerIMCs();
        ConfigHandler.instance().loadDefaultConfig();
        OverlayConfig.updateColors();
        initClientPacketInfo();
    }

    @Environment(EnvType.CLIENT)
    private static void initClientPacketInfo() {
        Waila.instance.registerPacketHandler("Waila", new Packet0x01TERequest(new Packet250CustomPayload()));
        Waila.instance.registerPacketHandler("Waila", new Packet0x02TENBTData(new Packet250CustomPayload()));
    }

    public boolean serverCustomPacketReceived(NetServerHandler handler, Packet250CustomPayload packet) {
        if (this.wailaPacketHandler == null) {
            this.wailaPacketHandler = new WailaPacketHandler();
        }
        return false;
    }

    @Override
    public void postSetup() {
        this.modID = modId;
        this.addonName = FabricLoader.getInstance().getModContainer(modID).get().getMetadata().getName();
        this.shouldVersionCheck = false;
        addResourcePackDomain(modId);
    }

    @Override
    public void initialize() {
    }

    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        PacketDispatcher.sendPacketToPlayer(Packet0x00ServerPing.create(), playerMP);
    }

    @Environment(EnvType.CLIENT)
    public boolean interceptCustomClientPacket(Minecraft mc, Packet250CustomPayload packet) {
        if (this.wailaPacketHandler == null) {
            this.wailaPacketHandler = new WailaPacketHandler();
        }
//        this.wailaPacketHandler.onPacketData(packet, mc.thePlayer);
        return false;
    }

    public void onInitialize() {
    }
}
