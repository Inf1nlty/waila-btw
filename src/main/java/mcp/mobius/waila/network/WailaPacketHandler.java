package mcp.mobius.waila.network;

import btw.block.tileentity.CampfireTileEntity;
import btw.block.tileentity.FiniteTorchTileEntity;
import btw.block.tileentity.OvenTileEntity;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import cn.xylose.waila.api.PacketDispatcher;
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
                }
            } catch (Exception e) {
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
                    TileEntity entity = server.worldServers[castedPacket.worldID].getBlockTileEntity(castedPacket.posX, castedPacket.posY, castedPacket.posZ);

                    ConfigHandler config = ConfigHandler.instance();
                    if (entity instanceof OvenTileEntity || entity instanceof CampfireTileEntity ||
                            entity instanceof TileEntitySkull || entity instanceof FiniteTorchTileEntity) {
                        if (entity instanceof OvenTileEntity) return;
                        else if (entity instanceof CampfireTileEntity) return;
                        else if (entity instanceof FiniteTorchTileEntity) return;
                        try {
                            NBTTagCompound tag = new NBTTagCompound();
                            entity.writeToNBT(tag);
                            PacketDispatcher.sendPacketToPlayer(Packet0x02TENBTData.create(tag), handler.playerEntity);
                        } catch (Throwable e) {
                            WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
                        }
                    }
                }
//                else if(header == 3) {
//                    Packet0x03ConfigData filePacket = new Packet0x03ConfigData(packet);
//                    Map<String, String> config = new HashMap<>();
//                    for (Object addon: AddonHandler.modList.values().toArray()) {
//                        if(Objects.equals(((BTWAddon) addon).getLanguageFilePrefix(), filePacket.file)) {
//                            config = ((BTWAddon)addon).loadConfigProperties();
//                            break;
//                        }
//                    }
//                    handler.playerEntity.sendChatToPlayer(config.toString());
//                }
            } catch (Exception ignored) {
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
