package cn.xylose.waila.addons.btw;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.src.*;

import java.util.List;

public class HUDHandlerBTWEntity implements IWailaEntityProvider {
    static EntitySpider spiderEntity = (EntitySpider) DataAccessorCommon.instance.entity;

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return spiderEntity;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        EntitySpider spider = (EntitySpider) entity;
        currenttip.add((spider).hasWeb() && config.getConfig("btw.spider_web") ? I18n.getString("info.btw.has_web") : null);
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return tag;
    }

    public static void register() {
        IWailaEntityProvider provider = new HUDHandlerBTWEntity();
        ModuleRegistrar.instance().addConfig("BTW", "info.btw.has_web");
        ModuleRegistrar.instance().registerBodyProvider(provider, EntitySpider.class);
    }
}
