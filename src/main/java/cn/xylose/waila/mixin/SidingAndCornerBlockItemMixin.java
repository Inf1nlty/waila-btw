package cn.xylose.waila.mixin;

import api.block.blocks.SidingAndCornerAndDecorativeBlock;
import btw.item.blockitems.SidingAndCornerBlockItem;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SidingAndCornerBlockItem.class)
public class SidingAndCornerBlockItemMixin extends ItemBlock {
    public SidingAndCornerBlockItemMixin(int par1) {
        super(par1);
    }

    /**
     * @author Xy_Luce
     * @reason Temporary fix
     */
    @Overwrite
    public String getUnlocalizedName(ItemStack itemstack) {
        if (itemstack.getItemDamage() == SidingAndCornerAndDecorativeBlock.SUBTYPE_BENCH) {
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("bench").toString();
        } else if (itemstack.getItemDamage() == SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE) {
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("fence").toString();
        } else if ((itemstack.getItemDamage() & 1 ) > 0) {
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("corner").toString();
        } else {
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("siding").toString();
        }
    }
}
