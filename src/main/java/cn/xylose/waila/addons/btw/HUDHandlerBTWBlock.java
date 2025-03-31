package cn.xylose.waila.addons.btw;

import btw.block.BTWBlocks;
import btw.block.blocks.CampfireBlock;
import btw.block.tileentity.CampfireTileEntity;
import btw.item.BTWItems;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.src.*;

import java.util.List;

import static net.minecraft.src.TileEntityFurnace.DEFAULT_COOK_TIME;

public class HUDHandlerBTWBlock implements IWailaDataProvider {
    static Block idleOven = BTWBlocks.idleOven;
    static Block burningOven = BTWBlocks.burningOven;
    static Block unlitCampfire = BTWBlocks.unlitCampfire;
    static Block smallCampfire = BTWBlocks.smallCampfire;
    static Block mediumCampfire = BTWBlocks.mediumCampfire;
    static Block largeCampfire = BTWBlocks.largeCampfire;
    static Block finiteTorch = BTWBlocks.finiteBurningTorch;

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        this.updateOven(currenttip, accessor, config);
        this.updateCampfire(currenttip, accessor, config);
        this.updateFiniteTorch(currenttip, accessor, config);
        return currenttip;
    }

    private List<String> updateOven(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (config.getConfig("btw.oven") && (block == burningOven || block == idleOven)) {
            NBTTagCompound tag = accessor.getNBTData();

            int remainingCookingTime = 0;

            if (tag.getTagList("Items").tagCount() != 0 && FurnaceRecipes.smelting().getSmeltingResult((((NBTTagCompound) tag.getTagList("Items").tagAt(0)).getShort("id"))) != null) {
                int iCookTimeShift = DEFAULT_COOK_TIME << FurnaceRecipes.smelting().getCookTimeBinaryShift(((NBTTagCompound) tag.getTagList("Items").tagAt(0)).getShort("id"));
                remainingCookingTime = iCookTimeShift * 4 - tag.getInteger("fcCookTimeEx");
            }
            int fuelTime = block == burningOven ? tag.getInteger("fcBurnTimeEx") : tag.getInteger("fcUnlitFuel");
            if (fuelTime != 0) {
                currenttip.add(I18n.getStringParams("info.btw.fuel_time", fuelTime / 20));
            }
            if (remainingCookingTime != 0) {
                currenttip.add(I18n.getStringParams("info.btw.cook_time", remainingCookingTime / 20));
            }
        }
        return currenttip;
    }

    private List<String> updateCampfire(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (config.getConfig("btw.campfire") && (block == smallCampfire || block == mediumCampfire || block == largeCampfire)) {

            final int TIME_TO_COOK = CampfireTileEntity.TIME_TO_COOK;
            final int TIME_TO_BURN_FOOD = CampfireTileEntity.TIME_TO_BURN_FOOD;

            NBTTagCompound tag = accessor.getNBTData();

            int burnTime = tag.getInteger("fcBurnCounter");
            if (burnTime != 0) {
                currenttip.add(I18n.getStringParams("info.btw.burn_time", burnTime / 20));
            }
            int cookCounter = tag.getInteger("fcCookCounter");
            if (cookCounter != 0 && TIME_TO_COOK - cookCounter != 0) {
                currenttip.add(I18n.getStringParams("info.btw.cook_time", (TIME_TO_COOK - cookCounter) / 20));
            }
            int burnedTime = (((CampfireBlock) accessor.getBlock()).fireLevel >= 3 &&
                    tag.getCompoundTag("fcCookStack").getShort("id") != BTWItems.burnedMeat.itemID) ?
                    (TIME_TO_BURN_FOOD - tag.getInteger("fcCookBurning")) / 20 : 0;
            if (burnedTime != 0) {
                currenttip.add(I18n.getStringParams("info.btw.burned_time", burnedTime));
            }
        }
        return currenttip;
    }

    private List<String> updateFiniteTorch(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (config.getConfig("btw.finite_torch") && block == finiteTorch) {
            NBTTagCompound tag = accessor.getNBTData();
            int time = tag.getInteger("fcBurnCounter");
            currenttip.add(I18n.getStringParams("info.btw.burn_time", time / 20));
        }
        return currenttip;
    }

//    private List<String> updateEntity(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        Entity block = accessor.en();
//        if (config.getConfig("btw.finite_torch") && block == finiteTorch) {
//            NBTTagCompound tag = accessor.getNBTData();
//            int time = tag.getInteger("fcBurnCounter");
//            currenttip.add(I18n.getStringParams("info.btw.burn_time", time / 20));
//        }
//        return currenttip;
//    }


    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        if (te != null) te.writeToNBT(tag);
        return tag;
    }

    public static void register() {
        IWailaDataProvider provider = new HUDHandlerBTWBlock();

        ModuleRegistrar.instance().addConfig("BTW", "btw.oven");
        ModuleRegistrar.instance().addConfig("BTW", "btw.campfire");
        ModuleRegistrar.instance().addConfig("BTW", "btw.finite_torch");

        ModuleRegistrar.instance().registerBodyProvider(provider, burningOven.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, largeCampfire.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, finiteTorch.getClass());

        ModuleRegistrar.instance().registerNBTProvider(provider, burningOven.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, largeCampfire.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, finiteTorch.getClass());
    }
}
