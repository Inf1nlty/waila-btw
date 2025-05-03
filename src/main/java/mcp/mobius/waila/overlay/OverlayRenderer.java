package mcp.mobius.waila.overlay;

import cn.xylose.waila.api.IBreakingProgress;
import net.minecraft.src.BossStatus;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;

public class OverlayRenderer {

    protected static boolean hasBlending;
    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasLight0;
    protected static boolean hasLight1;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static int boundTexIndex;
    private static int lastProgressLine = 0;
    private static int targetX = 0, targetY = 0, targetW = 0, targetH = 0;
    private static float currentX = 0, currentY = 0, currentW = 0, currentH = 0;
    private static float LERP_FACTOR = OverlayConfig.lerpfactor;
    private static float lastBreakProgress = 0F;
    private static float currentAlpha = 0F;
    private static final float FADE_SPEED = 0.1f;

    public void renderOverlay() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen == null && mc.theWorld != null
                && Minecraft.isGuiEnabled()
                && !mc.gameSettings.keyBindPlayerList.isPressed()
                && ConfigHandler.instance().showTooltip()
                && RayTracing.instance().getTarget() != null))
            return;

        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.TILE
                && RayTracing.instance().getTargetStack() != null) {
            renderOverlay(WailaTickHandler.instance().tooltip);
        }

        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.ENTITY
                && ConfigHandler.instance().getConfig("general.showents")) {
            renderOverlay(WailaTickHandler.instance().tooltip);
        }
    }

    public void renderOverlay(Tooltip tooltip) {
        GL11.glPushMatrix();
        saveGLState();

        if (tooltip != null) {
            currentAlpha = DisplayUtil.lerp(currentAlpha, 1F, FADE_SPEED);
            if (currentAlpha > 0.99F) {
                currentAlpha = 1F;
            }
        } else {
            currentAlpha = DisplayUtil.lerp(currentAlpha, 0F, FADE_SPEED);
            if (currentAlpha < 0.01F) {
                currentAlpha = 0F;
            }
        }

        if (currentAlpha <= 0F) {
            loadGLState();
            GL11.glPopMatrix();
            return;
        }

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
        tooltip.draw();
        GL11.glDisable(GL11.GL_BLEND);

        tooltip.draw2nd();

        if (tooltip.hasIcon) RenderHelper.enableGUIStandardItemLighting();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if (tooltip.hasIcon && tooltip.stack != null && tooltip.stack.getItem() != null)
            DisplayUtil.renderStack(tooltip.x + 5, tooltip.y + tooltip.h / 2 - 8, tooltip.stack);

        loadGLState();
        GL11.glPopMatrix();
    }

    public static void saveGLState() {
        hasBlending = GL11.glGetBoolean(GL11.GL_BLEND);
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }

    public static void loadGLState() {
        if (hasBlending) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (hasLight1) GL11.glEnable(GL11.GL_LIGHT1);
        else GL11.glDisable(GL11.GL_LIGHT1);
        if (hasDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
        GL11.glPopAttrib();
    }

    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        targetX = x;
        targetY = y;
        targetW = w;
        targetH = h;

        currentX = DisplayUtil.lerp(currentX, targetX, LERP_FACTOR);
        currentY = DisplayUtil.lerp(currentY, targetY, LERP_FACTOR);
        currentW = DisplayUtil.lerp(currentW, targetW, LERP_FACTOR);
        currentH = DisplayUtil.lerp(currentH, targetH, LERP_FACTOR);

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
