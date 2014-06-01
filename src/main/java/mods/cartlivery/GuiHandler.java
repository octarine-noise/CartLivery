package mods.cartlivery;

import mods.cartlivery.client.gui.GuiCutter;
import mods.cartlivery.common.container.ContainerCutter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0) return new ContainerCutter(player);
		return null;
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0) return new GuiCutter(player);
		return null;
	}

}
