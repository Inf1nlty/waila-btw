package mcp.mobius.waila.handlers;

import java.util.List;

import mcp.mobius.waila.utils.WailaLogger;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaFMPDecorator;
import mcp.mobius.waila.api.impl.DataAccessorFMP;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class DecoratorFMP implements IWailaBlockDecorator {

    @Override
    public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        NBTTagList list = accessor.getNBTData().getTagList("parts");
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound subtag = (NBTTagCompound) list.tagAt(i);
            String id = subtag.getString("id");

            if (ModuleRegistrar.instance().hasFMPDecorator(id)) {
                DataAccessorFMP.instance.set(
                        accessor.getWorld(),
                        accessor.getPlayer(),
                        accessor.getPosition(),
                        subtag,
                        id,
                        accessor.getRenderingPosition(),
                        accessor.getPartialFrame());

                for (List<IWailaFMPDecorator> providersList : ModuleRegistrar.instance().getFMPDecorators(id).values())
                    for (IWailaFMPDecorator provider : providersList)
                        provider.decorateBlock(itemStack, DataAccessorFMP.instance, config);
            }
        }
    }

    public static void register() {
        Class<?> BlockMultipart;
        try {
            BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
        } catch (ClassNotFoundException e) {
            Waila.log.log(WailaLogger.Level.WARN, "[FMP] Class not found. " + e);
            return;
        } catch (Exception e) {
            Waila.log.log(WailaLogger.Level.WARN, "[FMP] Unhandled exception." + e);
            return;
        }

        ModuleRegistrar.instance().registerDecorator(new DecoratorFMP(), BlockMultipart);
    }

}
