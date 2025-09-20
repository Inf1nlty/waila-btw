package mcp.mobius.waila.handlers;

import static mcp.mobius.waila.api.SpecialChars.BLUE;
import static mcp.mobius.waila.api.SpecialChars.GRAY;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.WHITE;
import static mcp.mobius.waila.api.SpecialChars.getRenderString;

import java.text.DecimalFormat;
import java.util.List;

import cn.xylose.waila.mixin.accessor.RenderAccessor;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.src.*;
import org.apache.commons.lang3.StringUtils;

public class HUDHandlerEntities implements IWailaEntityProvider {

    public static int nhearts = 20;
    public static float maxhpfortext = 40.0f;
    public static int nArmorIconsPerLine = 20;
    public static float maxArmorForText = 20.0f;

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
                                     IWailaConfigHandler config) {
        try {
            currenttip.add(WHITE + entity.getEntityName());
        } catch (Exception e) {
            currenttip.add(WHITE + "Unknown");
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
                                     IWailaConfigHandler config) {
        this.getEntityHeath(entity, currenttip, accessor, config);
        this.getEntityArmor(entity, currenttip, accessor, config);
        this.getEntityAttack(entity, currenttip, accessor, config);
        this.getAnimalBreedAndGrowthInfo(entity, currenttip, accessor, config);
        return currenttip;
    }

    public void getEntityHeath(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
                               IWailaConfigHandler config) {
        if (!config.getConfig("general.showhp")) return;

        if (entity instanceof EntityLivingBase) {

            nhearts = nhearts <= 0 ? 20 : nhearts;

            float health = ((EntityLivingBase) entity).getHealth() / 2.0f;
            float maxhp = ((EntityLivingBase) entity).getMaxHealth() / 2.0f;
            if (maxhp <= 0) return;

            if (((EntityLivingBase) entity).getMaxHealth() > maxhpfortext) currenttip.add(
                    String.format(
                            "HP : " + WHITE + "%.0f" + GRAY + " / " + WHITE + "%.0f",
                            ((EntityLivingBase) entity).getHealth(),
                            ((EntityLivingBase) entity).getMaxHealth()));

            else {
                currenttip.add(
                        getRenderString(
                                "waila.health",
                                String.valueOf(nhearts),
                                String.valueOf(health),
                                String.valueOf(maxhp)));
            }
        }
    }

    public void getEntityArmor(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
                               IWailaConfigHandler config) {
        if (!config.getConfig("general.showarmor")) return;

        if (entity instanceof EntityLivingBase entityLivingBase) {

            float armor = entityLivingBase.getTotalArmorValue();
            if (armor <= 0) return;

            if (armor > maxArmorForText) {
                currenttip.add(
                        String.format(LangUtil.translateG("hud.msg.armor", armor))
                );
            } else {
                currenttip.add(
                        getRenderString(
                                "waila.armor",
                                String.valueOf(nArmorIconsPerLine),
                                String.valueOf(armor),
                                String.valueOf(armor)));
            }
        }
    }

    public void getEntityAttack(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
                                IWailaConfigHandler config) {
        if (!config.getConfig("general.showatk")) return;

        if (entity instanceof EntityLivingBase entityLivingBase) {
            float total_melee_damage;
            DecimalFormat damageFormat = new DecimalFormat("0.00");
            AttributeInstance attackDamage = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.attackDamage);
            if (attackDamage != null) {
                total_melee_damage = Float.parseFloat(damageFormat.format((float) attackDamage.getAttributeValue()));
                currenttip.add(LangUtil.translateG("hud.msg.attack", total_melee_damage));
            }
        }
    }

    public void getAnimalBreedAndGrowthInfo(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("general.showbreed") || !(entity instanceof EntityAnimal)) return;

        NBTTagCompound tag = accessor.getNBTData();

        if (tag == null) return;

        if (tag.hasKey("AnimalGrowingAge")) {

            int growingAge = tag.getInteger("AnimalGrowingAge");

            if (growingAge > 0) {

                int seconds = growingAge / 20;
                currenttip.add(GRAY + LangUtil.translateG("hud.msg.breed_cooldown", seconds));
            }
        }
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
            IWailaConfigHandler config) {
        if (!config.getConfig("general.showmods")) return currenttip;

        try {
            currenttip.add(BLUE + ITALIC + getEntityMod(entity));
        } catch (Exception e) {
            currenttip.add(BLUE + ITALIC + "Unknown");
        }
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity te, NBTTagCompound tag, World world) {
        return tag;
    }

    private static String getEntityMod(Entity entity) {
        String modName = Waila.modsName;
//        try {
//            EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
//            ModContainer modC = er.getContainer();
//            modsName = modC.getName();
//        } catch (NullPointerException e) {
//            modsName = "Minecraft";
//        }
        if (entity.worldObj.isRemote) {
            Render render = RenderManager.instance.getEntityRenderObject(entity);

            if (render instanceof RenderLiving renderLiving) {
                ResourceLocation resourceLocation = ((RenderAccessor) renderLiving).getEntityTexture(entity);
                return StringUtils.capitalize(resourceLocation.getResourceDomain());
            }
        }
        return modName;
    }

}
