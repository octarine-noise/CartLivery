package mods.cartlivery.client.gui;

import mods.cartlivery.CommonProxy;
import mods.cartlivery.client.LiveryTextureRegistry;
import mods.cartlivery.client.model.ModelCartLivery;
import mods.cartlivery.common.container.ContainerCutter;
import mods.cartlivery.common.network.LiveryGuiPatternPacket;
import mods.cartlivery.common.utils.ColorUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiCutter extends InventoryEffectRenderer {

	public static ResourceLocation texture = new ResourceLocation("cartlivery", "textures/gui/cutter.png");
	public static ModelCartLivery cartModel = new ModelCartLivery();
	
	ContainerCutter container;
	
	static int baseColor = 7;
	static int patternColor = 7;
	String pattern = "";
	
	public GuiCutter(EntityPlayer player) {
		super(new ContainerCutter(player));
		container = (ContainerCutter) inventorySlots;
		CommonProxy.network.sendPacketData(new LiveryGuiPatternPacket(pattern));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 58, 20, 20, "<-"));
		buttonList.add(new GuiButton(1, guiLeft + 58, guiTop + 58, 20, 20, "->"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        drawCart(mouseX, mouseY, baseColor, patternColor);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		pattern = LiveryTextureRegistry.cycle(pattern, button.id == 1);
		container.pattern = pattern;
		container.onCraftMatrixChanged(container.inventoryInput);
		CommonProxy.network.sendPacketData(new LiveryGuiPatternPacket(pattern));
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		int xRel = x - guiLeft;
		int yRel = y - guiTop;
		if (xRel > 7 && xRel < 78 && yRel > 7 && yRel < 78 && container.player.inventory.getItemStack() != null) {
			int dyeColor = ColorUtils.getDyeColor(container.player.inventory.getItemStack());
			if (dyeColor != -1 && button == 0) baseColor = dyeColor;
			if (dyeColor != -1 && button == 1) patternColor = dyeColor;
		}
		super.mouseClicked(x, y, button);
	}

	protected void drawCart(int mouseX, int mouseY, int baseColor, int patternColor) {
		float x = guiLeft + 42.0f;
		float y = guiTop + 30.0f;
		float depth = 50.0f;
		float scale = 30.0f;
		
		float yawRot = 90.0f + (float) (mouseX * 180 / width); 
		float pitchRot = -30.0f + (float) (mouseY * 90 / height);
		
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, depth);
        GL11.glScalef(-scale, scale, scale);
        GL11.glTranslatef(0.0F, 0.35f, 0.0F);
        GL11.glRotatef(yawRot, 0.0F, -1.0F, 0.0F);
        GL11.glRotatef(pitchRot, 1.0F, 0.0F, 0.0F);
        cartModel.render(baseColor, patternColor, pattern, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
	}
	
	
}
