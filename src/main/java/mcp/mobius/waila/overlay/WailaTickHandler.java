package mcp.mobius.waila.overlay;

import static mcp.mobius.waila.api.SpecialChars.ITALIC;

import java.util.HashSet;
import java.util.List;

import cn.xylose.waila.api.PacketDispatcher;
import mcp.mobius.waila.network.Packet0x03EntRequest;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.cbcore.Layout;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.src.*;
import net.minecraftforge.common.Configuration;
import org.lwjgl.input.Keyboard;

public class WailaTickHandler {

    private Tooltip tooltip = null;
    private final MetaDataProvider handler = new MetaDataProvider();

    public void tickRender() {
        OverlayRenderer.renderOverlay(tooltip);
    }

    public void tickClient() {

        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;
        if (world == null || player == null) {
            this.tooltip = null;
            RayTracing.instance().clear();
            return;
        }

        RayTracing.instance().fire();
        MovingObjectPosition target = RayTracing.instance().getTarget();

        List<String> currenttip;
        List<String> currenttipHead;
        List<String> currenttipBody;
        List<String> currenttipTail;
        if (target != null && target.typeOfHit == EnumMovingObjectType.TILE) {
            DataAccessorCommon accessor = DataAccessorCommon.instance;
            accessor.set(world, player, target);
            ItemStack targetStack = RayTracing.instance().getTargetStack(); // Here we get either the proper stack
            // or the override

            if (targetStack != null) {
                currenttip = new TipList<String, String>();
                currenttipHead = new TipList<String, String>();
                currenttipBody = new TipList<String, String>();
                currenttipTail = new TipList<String, String>();

                currenttipHead = handler.handleBlockTextData(
                        targetStack,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipHead,
                        Layout.HEADER);
                currenttipBody = handler.handleBlockTextData(
                        targetStack,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipBody,
                        Layout.BODY);
                currenttipTail = handler.handleBlockTextData(
                        targetStack,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipTail,
                        Layout.FOOTER);

                if (ConfigHandler.instance()
                        .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false)
                        && !currenttipBody.isEmpty()
                        && !accessor.getPlayer().isSneaking()) {
                    currenttipBody.clear();
                    currenttipBody.add(ITALIC + "Press shift for more data");
                }


                currenttip.addAll(currenttipHead);
                currenttip.addAll(currenttipBody);
                currenttip.addAll(currenttipTail);

                this.tooltip = new Tooltip(currenttip, targetStack);
            }
        } else if (target != null && target.typeOfHit == EnumMovingObjectType.ENTITY) {
            DataAccessorCommon accessor = DataAccessorCommon.instance;
            accessor.set(world, player, target);

            Entity targetEnt = RayTracing.instance().getTargetEntity(); // This need to be replaced by the override
            // check.

            if (targetEnt != null) {
                currenttip = new TipList<String, String>();
                currenttipHead = new TipList<String, String>();
                currenttipBody = new TipList<String, String>();
                currenttipTail = new TipList<String, String>();

                currenttipHead = handler.handleEntityTextData(
                        targetEnt,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipHead,
                        Layout.HEADER);
                currenttipBody = handler.handleEntityTextData(
                        targetEnt,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipBody,
                        Layout.BODY);
                currenttipTail = handler.handleEntityTextData(
                        targetEnt,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipTail,
                        Layout.FOOTER);

                if (ConfigHandler.instance()
                        .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTENTS, false)
                        && !currenttipBody.isEmpty()
                        && !accessor.getPlayer().isSneaking()) {
                    currenttipBody.clear();
                    currenttipBody.add(ITALIC + "Press shift for more data");
                }

                currenttip.addAll(currenttipHead);
                currenttip.addAll(currenttipBody);
                currenttip.addAll(currenttipTail);

                this.tooltip = new Tooltip(currenttip, false);
            }
        }

    }
}