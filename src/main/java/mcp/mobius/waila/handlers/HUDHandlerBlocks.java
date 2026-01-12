package mcp.mobius.waila.handlers;

import java.util.List;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.src.*;
import net.minecraftforge.common.Configuration;

public class HUDHandlerBlocks implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        String name = null;
        try {
            String s = DisplayUtil.itemDisplayNameShort(itemStack);
            if (s != null && !s.endsWith("Unnamed")) name = s;

            if (name != null) currenttip.add(name);
        } catch (Exception ignored) {}

        if (currenttip.isEmpty() || (name == null || name.endsWith("Unnamed")) || itemStack.getItem() == null) {
            ItemStack fallbackStack = findFallbackItemStackForBlock(accessor.getBlock(), accessor.getMetadata());
            if (fallbackStack != null && fallbackStack.getItem() != null) {
                String fallbackName = DisplayUtil.itemDisplayNameShort(fallbackStack);
                if (fallbackName != null && !fallbackName.endsWith("Unnamed")) {
                    currenttip.add(fallbackName);
                    itemStack = fallbackStack;
                } else {
                    currenttip.add("< Unnamed >");
                }
            } else {
                currenttip.add("< Unnamed >");
            }
        }

        if (itemStack.getItem() == Item.redstone) {
            int md = accessor.getMetadata();
            String s = "" + md;
            if (s.length() < 2) s = " " + s;
            currenttip.set(currenttip.size() - 1, name + " " + s);
        }

        if (currenttip.isEmpty()) currenttip.add("\u00a7r" + String.format(FormattingConfig.blockFormat, "< Unnamed >"));
        else {
            if (ConfigHandler.instance()
                    .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true)
                    && !Strings.isNullOrEmpty(FormattingConfig.metaFormat)) {
                currenttip.add(
                        String.format(
                                "\u00a7r" + String.format(FormattingConfig.metaFormat,
                                        accessor.getBlockID(),
                                        accessor.getMetadata(),
                                        accessor.getBlockQualifiedName())));
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        if (!ConfigHandler.instance().showMods()) return currenttip;

        //currenttip.add(RENDER + "{Plip}" + RENDER + "{Plop,thisisatest,222,333}");

        String modName = ModIdentification.nameFromStack(itemStack);
        if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat))
            currenttip.add(String.format(FormattingConfig.modNameFormat, modName));

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
                                     int y, int z) {
        return tag;
    }

    public static ItemStack findFallbackItemStackForBlock(Block block, int metadata) {
        String blockName = block.getUnlocalizedName();
        for (Item item : Item.itemsList) {
            if (item == null) continue;
            String itemName = item.getUnlocalizedName();
            if (itemName != null && blockName != null &&
                    itemName.replace("item.", "").equals(blockName.replace("tile.", ""))) {
                return new ItemStack(item, 1, metadata);
            }
        }
        return null;
    }
}
