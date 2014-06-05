package mods.cartlivery.common.network;

import net.minecraft.inventory.Container;
import mods.cartlivery.common.container.ContainerCutter;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LiveryGuiPatternHandler implements IMessageHandler<LiveryGuiPatternMessage, IMessage> {

	public IMessage onMessage(LiveryGuiPatternMessage message, MessageContext ctx) {
		Container container = ctx.getServerHandler().playerEntity.openContainer;
		if (container != null && container instanceof ContainerCutter) {
			ContainerCutter cutter = (ContainerCutter) container;
			cutter.pattern = message.pattern;
			cutter.onCraftMatrixChanged(cutter.inventoryInput);
		}
		return null;
	}

}
