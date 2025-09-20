package mcp.mobius.waila.utils;

import cn.xylose.waila.mixin.accessor.BlockAccessor;
import mcp.mobius.waila.Waila;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class ModIdentification {

    public static HashMap<String, String> modSource_Name = new HashMap<>();
    public static HashMap<String, String> modSource_ID = new HashMap<>();

    public static void init() {

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            modSource_Name.put(mod.getMetadata().getName(), mod.getMetadata().getName());
            modSource_ID.put(mod.getMetadata().getName(), mod.getMetadata().getId());
        }

        // TODO : Update this to match new version (1.7.2)
        modSource_Name.put("1.6.2.jar", "Minecraft");
        modSource_Name.put("1.6.3.jar", "Minecraft");
        modSource_Name.put("1.6.4.jar", "Minecraft");
        modSource_Name.put("1.7.2.jar", "Minecraft");
        modSource_Name.put("Forge", "Minecraft");
        modSource_ID.put("1.6.2.jar", "Minecraft");
        modSource_ID.put("1.6.3.jar", "Minecraft");
        modSource_ID.put("1.6.4.jar", "Minecraft");
        modSource_ID.put("1.7.2.jar", "Minecraft");
        modSource_ID.put("Forge", "Minecraft");
    }

    public static String nameFromObject(Object obj) {
        String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();

        try {
            objPath = URLDecoder.decode(objPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String modName = "<Unknown>";
        for (String s : modSource_Name.keySet()) if (objPath.contains(s)) {
            modName = modSource_Name.get(s);
            break;
        }

        if (modName.equals("Minecraft Coder Pack")) modName = "Minecraft";

        return modName;
    }

    public static String nameFromStack(ItemStack stack) {
//        try {
//            ModContainer mod = GameData.findModOwner(GameData.itemRegistry.getNameForObject(stack.getItem()));
//            return mod == null ? "Minecraft" : mod.getName();
//        } catch (NullPointerException e) {
//            return "";
//        }
        if (stack.getItem() instanceof ItemBlock) {
            Block block = Block.blocksList[((ItemBlock) stack.getItem()).getBlockID()];
            String textureName = ((BlockAccessor) block).getTextureName();
            if (textureName.contains(":")) {
                String[] parts = textureName.split(":");
                return StringUtils.capitalize(parts[0]);
            }
        }
        return Waila.modsName;
    }
}
