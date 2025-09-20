package mcp.mobius.waila.network;

import cn.xylose.waila.api.PacketDispatcher;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class WailaPacketHandler {

    public void handleCustomPacket(Packet250CustomPayload packet) {
        if (packet.channel.equals("Waila")) {
            try {
                byte header = getHeader(packet);
                if (header == 0) {
                    Waila.log.info("Received server authentication msg. Remote sync will be activated");
                    Waila.instance.serverPresent = true;
                } else if (header == 2) {
                    Packet0x02TENBTData castedPacket = new Packet0x02TENBTData(packet);
                    DataAccessorCommon.instance.remoteNbt = castedPacket.tag;
                } else if (header == 4) {
                    Packet0x04EntNBTData castedPacket = new Packet0x04EntNBTData(packet);
                    DataAccessorCommon.instance.setNBTData(castedPacket.tag);
                }
            } catch (Exception ignored) {
                // Swallowing exception: non-fatal packet parse error or old protocol, safe to ignore
            }
        }
    }

    public void handleCustomPacket(NetServerHandler handler, Packet250CustomPayload packet) {
        if (packet.channel.equals("Waila")) {
            try {
                byte header = getHeader(packet);
                if (header == 1) {
                    Packet0x01TERequest castedPacket = new Packet0x01TERequest(packet);
                    MinecraftServer server = MinecraftServer.getServer();
                    Block block = Block.blocksList[server.worldServers[castedPacket.worldID].getBlockId(castedPacket.posX, castedPacket.posY, castedPacket.posZ)];
                    TileEntity tileEntity = server.worldServers[castedPacket.worldID].getBlockTileEntity(castedPacket.posX, castedPacket.posY, castedPacket.posZ);

                    if (ModuleRegistrar.instance().hasNBTProviders(block)
                            || ModuleRegistrar.instance().hasNBTProviders(tileEntity))
                        try {
                            NBTTagCompound tag = new NBTTagCompound();
                            tileEntity.writeToNBT(tag);
                            PacketDispatcher.sendPacketToPlayer(Packet0x02TENBTData.create(tag), handler.playerEntity);
                        } catch (Throwable e) {
                            WailaExceptionHandler.handleErr(e, tileEntity.getClass().toString(), null);
                        }
                }
                else if (header == 3) {
                    Packet0x03EntRequest castedPacket = new Packet0x03EntRequest(packet);
                    MinecraftServer server = MinecraftServer.getServer();
                    World world = server.worldServers[castedPacket.worldID];
                    Entity ent = null;
                    for (Object obj : world.loadedEntityList) {
                        if (((Entity) obj).entityId == castedPacket.id) {
                            ent = (Entity) obj;
                        }
                    }

                    if (ent != null && ModuleRegistrar.instance().hasNBTEntityProviders(ent)) {
                        try {
                            NBTTagCompound tag = new NBTTagCompound();
                            tag.setInteger("WailaEntityID", ent.entityId);
                            for (java.util.List<IWailaEntityProvider> provList : ModuleRegistrar.instance().getNBTEntityProviders(ent).values()) {
                                for (IWailaEntityProvider prov : provList) {
                                    prov.getNBTData(handler.playerEntity, ent, tag, world);
                                }
                            }
                            PacketDispatcher.sendPacketToPlayer(Packet0x04EntNBTData.create(tag), handler.playerEntity);
                        }
                        catch (Throwable e) {
                            Waila.log.warn("Exception in entity NBT provider: " + e);
                        }
                    }
                }
            } catch (Exception ignored) {
                // Swallowing exception: non-fatal packet parse error or old protocol, safe to ignore
            }
        }
    }

    public byte getHeader(Packet250CustomPayload packet) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            return inputStream.readByte();
        } catch (IOException e) {
            return (byte) -1;
        }
    }
}