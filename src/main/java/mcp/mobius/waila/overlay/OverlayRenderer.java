package mcp.mobius.waila.overlay;

import cn.xylose.waila.api.IBreakingProgress;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;

import java.util.List;

public class OverlayRenderer {

    protected static boolean hasBlending;
    protected static boolean hasDepthTest;
    protected static int boundTexIndex;
    private static float currentX = 0, currentY = 0, currentW = 0, currentH = 0;
    private static int lastProgressLine = 0;
    private static float lastBreakProgress = 0F;

    private static boolean isExample = false;

    public static void renderOverlay(Tooltip tooltip) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen == null && mc.theWorld != null
                && Minecraft.isGuiEnabled()
                && !mc.gameSettings.keyBindPlayerList.isPressed()
                && ConfigHandler.instance().showTooltip()
                && RayTracing.instance().getTarget() != null))
            return;

        isExample = false;

        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.TILE
                && RayTracing.instance().getTargetStack() != null) {
            doRenderOverlay(tooltip);
        }

        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.ENTITY
                && ConfigHandler.instance().getConfig("general.showents")) {
            doRenderOverlay(tooltip);
        }
    }

    public static void renderOverlayExample() {
        isExample = true;
        ItemStack exampleStack = new ItemStack(Block.grass);
        List<String> currenttip = new TipList<String, String>();
        List<String> currenttipHead = new TipList<String, String>();
        List<String> currenttipBody = new TipList<String, String>();
        List<String> currenttipTail = new TipList<String, String>();

        currenttipHead.add(String.format(FormattingConfig.blockFormat, DisplayUtil.itemDisplayNameShort(exampleStack)));
        if (ConfigHandler.instance().showMods())
            currenttipTail.add(String.format(FormattingConfig.modNameFormat, ModIdentification.nameFromStack(exampleStack)));

        currenttip.addAll(currenttipHead);
        currenttip.addAll(currenttipBody);
        currenttip.addAll(currenttipTail);

        Tooltip exampleTip = new Tooltip(currenttip, exampleStack);
        doRenderOverlay(exampleTip);
    }

    private static void doRenderOverlay(Tooltip tooltip) {
        Minecraft client = Minecraft.getMinecraft();

        GL11.glPushMatrix();
        saveGLState();

        if (BossStatus.bossName != null && BossStatus.statusBarLength > 0) tooltip.y += 20;

        GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0F);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawTooltipBox(
                tooltip.x,
                tooltip.y,
                tooltip.w,
                tooltip.h,
                OverlayConfig.bgcolor,
                OverlayConfig.gradient1,
                OverlayConfig.gradient2);

        drawBreakProgress(
                tooltip.x,
                tooltip.y,
                tooltip.w,
                tooltip.h);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
//        int scale = new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight).getScaleFactor();
//        GL11.glScissor(
//                tooltip.x * scale,
//                client.displayHeight - tooltip.h * scale,
//                (tooltip.w - tooltip.x) * scale,
//                (tooltip.h - tooltip.y) * scale);
//        Waila.logger.debug("Scissor: " + tooltip.x + " " + tooltip.y + " " + tooltip.w + " " + tooltip.h);
        tooltip.draw();
//        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        tooltip.draw2nd();

        if (tooltip.hasIcon) RenderHelper.enableGUIStandardItemLighting();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if (tooltip.hasIcon && tooltip.stack != null && tooltip.stack.getItem() != null)
            DisplayUtil.renderStack(tooltip.x + 5, tooltip.y + tooltip.h / 2 - 8, tooltip.stack);

        loadGLState();
        GL11.glPopMatrix();
    }

    private static void saveGLState() {
        hasBlending = GL11.glGetBoolean(GL11.GL_BLEND);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }

    private static void loadGLState() {
        if (hasBlending) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (hasDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
        GL11.glPopAttrib();
    }

    private static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        float lerpFactor = isExample ? 1.0F : OverlayConfig.lerpfactor;

        currentX = DisplayUtil.lerp(currentX, x, lerpFactor);
        currentY = DisplayUtil.lerp(currentY, y, lerpFactor);
        currentW = DisplayUtil.lerp(currentW, w, lerpFactor);
        currentH = DisplayUtil.lerp(currentH, h, lerpFactor);

        int drawX = (int) currentX;
        int drawY = (int) currentY;
        int drawW = (int) currentW;
        int drawH = (int) currentH;

        DisplayUtil.drawGradientRect(drawX + 1, drawY + 1, drawW - 1, drawH - 1, bg, bg); // center
        DisplayUtil.drawGradientRect(drawX + 1, drawY, drawW - 1, 1, bg, bg); // top frame
        DisplayUtil.drawGradientRect(drawX + 1, drawY + drawH, drawW - 1, 1, bg, bg); // bottom frame
        DisplayUtil.drawGradientRect(drawX, drawY + 1, 1, drawH - 1, bg, bg); // left frame
        DisplayUtil.drawGradientRect(drawX + drawW, drawY + 1, 1, drawH - 1, bg, bg); // right frame
        DisplayUtil.drawGradientRect(drawX + 1, drawY + 2, 1, drawH - 3, grad1, grad2); // left gradient
        DisplayUtil.drawGradientRect(drawX + drawW - 1, drawY + 2, 1, drawH - 3, grad1, grad2); // right gradient
        DisplayUtil.drawGradientRect(drawX + 1, drawY + 1, drawW - 1, 1, grad1, grad1); // top gradient
        DisplayUtil.drawGradientRect(drawX + 1, drawY + drawH - 1, drawW - 1, 1, grad2, grad2); // bottom gradient
    }

    public static void drawBreakProgress(int x, int y, int w, int h) {
        float breakProgress;
        if (Minecraft.getMinecraft().playerController != null) {
            breakProgress = ((IBreakingProgress) Minecraft.getMinecraft().playerController).getCurrentBreakingProgress();
            int currentProgressLine = 0;

            if (breakProgress > 0.0F) {
                int progress = (int) (breakProgress * 100.0F);
                currentProgressLine = (int) (progress / 100.0 * w);
                lastProgressLine = currentProgressLine;
                lastBreakProgress = breakProgress;
            } else {
                if (lastBreakProgress > 0.0F) {
                    lastProgressLine = (int) (lastProgressLine * 0.9f);
                    currentProgressLine = lastProgressLine;
                    if (currentProgressLine < 1) {
                        currentProgressLine = 0;
                        lastBreakProgress = 0.0F;
                    }
                }
            }

            if (currentProgressLine > 0) {
                DisplayUtil.drawGradientRect(x, y + h - 1, currentProgressLine, 1, 0xFF74766B, 0xFF74766B);
            }
        }
    }
}
