package cn.xylose.waila.handlers.emi;

import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.stack.EmiStack;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;

import java.util.Objects;

public class EMIHandler {
    static Minecraft mc = Minecraft.getMinecraft();

    public static EmiStack updateEmiStack() {
        return EmiStack.of(new ItemStack(Block.blocksList[mc.theWorld.getBlockId(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ)]));
    }

    public static void displayRecipes() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit == null) {
            EmiApi.displayRecipes(Objects.requireNonNull(EMIHandler.updateEmiStack()));
        }
    }

    public static void displayUses() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit == null) {
            EmiApi.displayUses(Objects.requireNonNull(EMIHandler.updateEmiStack()));
        }
    }
}
