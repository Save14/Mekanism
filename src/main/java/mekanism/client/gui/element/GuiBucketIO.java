package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiBucketIO extends GuiElement {

    public GuiBucketIO(IGuiWrapper gui, ResourceLocation def) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiBucket.png"), gui, def);
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + 176, guiHeight + 66, 26, 57);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        minecraft.textureManager.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + 176, guiHeight + 66, 0, 0, 26, 57);
        minecraft.textureManager.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        minecraft.textureManager.bindTexture(RESOURCE);
        minecraft.textureManager.bindTexture(defaultLocation);
    }

    @Override
    protected boolean inBounds(double xAxis, double yAxis) {
        return xAxis >= 180 && xAxis <= 196 && yAxis >= 71 && yAxis <= 87 || xAxis >= 180 && xAxis <= 196 && yAxis >= 102 && yAxis <= 118;
    }

    @Override
    public boolean preMouseClicked(double mouseX, double mouseY, int button) {
        if (inBounds(mouseX, mouseY)) {
            offsetX(26);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inBounds(mouseX, mouseY)) {
            offsetX(-26);
        }
    }
}