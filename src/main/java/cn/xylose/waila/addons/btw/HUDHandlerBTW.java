package cn.xylose.waila.addons.btw;

import btw.block.BTWBlocks;
import btw.block.blocks.CampfireBlock;
import btw.block.tileentity.CampfireTileEntity;
import btw.block.tileentity.FiniteTorchTileEntity;
import btw.block.tileentity.OvenTileEntity;
import btw.item.BTWItems;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.src.*;

import java.util.List;

import static net.minecraft.src.TileEntityFurnace.DEFAULT_COOK_TIME;

public class HUDHandlerBTW implements IWailaDataProvider {

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
        if (config.getConfig("btw.oven") && (block == burningOven) && accessor.getTileEntity() instanceof OvenTileEntity) {
            NBTTagCompound tag = accessor.getNBTData();

            int remainingCookingTime = 0;

            if (tag.getTagList("Items").tagCount() != 0 && FurnaceRecipes.smelting().getSmeltingResult((((NBTTagCompound) tag.getTagList("Items").tagAt(0)).getShort("id"))) != null) {
                int iCookTimeShift = DEFAULT_COOK_TIME << FurnaceRecipes.smelting().getCookTimeBinaryShift(((NBTTagCompound) tag.getTagList("Items").tagAt(0)).getShort("id"));
                remainingCookingTime = iCookTimeShift * 4 - tag.getInteger("fcCookTimeEx");
            }
            currenttip.add(String.format("FuelTime: %s", (block == burningOven ? tag.getInteger("fcBurnTimeEx") : tag.getInteger("fcUnlitFuel")) / 20));
            currenttip.add(String.format("CookTime: %s", remainingCookingTime / 20));
        }
        return currenttip;
    }

    private List<String> updateCampfire(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (config.getConfig("btw.campfire") && (block == smallCampfire || block == mediumCampfire || block == largeCampfire) && accessor.getTileEntity() instanceof CampfireTileEntity) {
            NBTTagCompound tag = accessor.getNBTData();

            int TIME_TO_COOK = (DEFAULT_COOK_TIME * 8 * 3 / 2);
            int TIME_TO_BURN_FOOD = (TIME_TO_COOK / 2);

            if (tag.getInteger("fcCookCounter") == 0) TIME_TO_COOK = 0;

            currenttip.add(String.format("BurnTime: %s", tag.getInteger("fcBurnCounter") / 20));
            currenttip.add(String.format("CookTime: %s", (TIME_TO_COOK - tag.getInteger("fcCookCounter")) / 20));
            currenttip.add(String.format("BurnedTime: %s", (((CampfireBlock) accessor.getBlock()).fireLevel >= 3 && tag.getCompoundTag("fcCookStack").getShort("id") != BTWItems.burnedMeat.itemID) ? (TIME_TO_BURN_FOOD - tag.getInteger("fcCookBurning")) / 20 : "null"));
        }
        return currenttip;
    }

    private List<String> updateFiniteTorch(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (config.getConfig("btw.finite_torch") && block == finiteTorch && accessor.getTileEntity() instanceof FiniteTorchTileEntity) {
            NBTTagCompound tag = accessor.getNBTData();
            int time = tag.getInteger("fcBurnCounter");
            currenttip.add(String.format("Time: %s", time / 20));
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

        ModuleRegistrar.instance().registerBodyProvider(provider, idleOven.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, burningOven.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, unlitCampfire.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, smallCampfire.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, mediumCampfire.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, largeCampfire.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider,  finiteTorch.getClass());
    }
}
