package mcp.mobius.waila.network;

import net.minecraft.src.Entity;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

import java.io.*;
import java.util.HashSet;

public class Packet0x03EntRequest {
    public byte header;
    public int worldID;
    public int id;
//    public HashSet<String> keys = new HashSet<>();

    public Packet0x03EntRequest(Packet250CustomPayload packet) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
//        this.keys.clear();

        try {
            this.header = inputStream.readByte();
            this.worldID = inputStream.readInt();
            this.id = inputStream.readInt();
//            int nkeys = inputStream.readInt();

//            for(int i = 0; i < nkeys; ++i) {
//                this.keys.add(Packet.readString(inputStream, 250));
//            }
        } catch (IOException ignored) {
        }

    }

    public static Packet250CustomPayload create(World world, Entity ent, HashSet<String> keys) {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(17);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            outputStream.writeByte(3);
            outputStream.writeInt(world.provider.dimensionId);
            outputStream.writeInt(ent.entityId);
            outputStream.writeInt(keys.size());

//            for (String key : keys) {
//                Packet.writeString(key, outputStream);
//            }
        } catch (IOException ignored) {
        }

        packet.channel = "Waila";
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        return packet;
    }
}
