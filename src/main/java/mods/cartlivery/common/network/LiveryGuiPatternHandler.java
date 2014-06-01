package mods.cartlivery.common.network;

import mods.cartlivery.common.container.ContainerCutter;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LiveryGuiPatternHandler implements IMessageHandler<LiveryGuiPatternMessage, NopMessage> {

	public NopMessage onMessage(LiveryGuiPatternMessage message, MessageContext ctx) {
		if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerCutter) {
			ContainerCutter container = (ContainerCutter) ctx.getServerHandler().playerEntity.openContainer;
			container.pattern = message.pattern;
			container.onCraftMatrixChanged(container.inventoryInput);
		}
		return null;
	}

}
