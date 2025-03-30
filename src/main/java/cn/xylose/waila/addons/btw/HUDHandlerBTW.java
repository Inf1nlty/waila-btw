package cn.xylose.waila.addons.btw;

import btw.block.BTWBlocks;
import btw.block.blocks.CampfireBlock;
import btw.block.blocks.FiniteBurningTorchBlock;
import btw.block.blocks.OvenBlock;
import btw.block.tileentity.CampfireTileEntity;
import btw.item.BTWItems;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.src.*;

import java.util.List;

import static net.minecraft.src.TileEntityFurnace.DEFAULT_COOK_TIME;

public class HUDHandlerBTW implements IWailaDataProvider {
    static int idleOven = BTWBlocks.idleOven.blockID;
    static int burningOven = BTWBlocks.burningOven.blockID;
    static int unlitCampfire = BTWBlocks.unlitCampfire.blockID;
    static int smallCampfire = BTWBlocks.smallCampfire.blockID;
    static int mediumCampfire = BTWBlocks.mediumCampfire.blockID;
    static int largeCampfire = BTWBlocks.largeCampfire.blockID;
    static int finiteTorch = BTWBlocks.finiteBurningTorch.blockID;

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
        int block = accessor.getBlockID();
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
        int block = accessor.getBlockID();
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
        int block = accessor.getBlockID();
        if (config.getConfig("btw.finite_torch") && block == finiteTorch) {
            NBTTagCompound tag = accessor.getNBTData();
            int time = tag.getInteger("fcBurnCounter");
            currenttip.add(I18n.getStringParams("info.btw.burn_time", time / 20));
        }
        return currenttip;
    }


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
        ModuleRegistrar.instance().addConfig("BTW", "btw.oven");
        ModuleRegistrar.instance().addConfig("BTW", "btw.campfire");
        ModuleRegistrar.instance().addConfig("BTW", "btw.finite_torch");

        IWailaDataProvider provider = new HUDHandlerBTW();

        ModuleRegistrar.instance().registerBodyProvider(provider, OvenBlock.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, CampfireBlock.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, FiniteBurningTorchBlock.class);

        ModuleRegistrar.instance().registerNBTProvider(provider, OvenBlock.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, CampfireBlock.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, FiniteBurningTorchBlock.class);
    }
}
