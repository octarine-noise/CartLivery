package mods.cartlivery.common.network;

import mods.cartlivery.common.CartLivery;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LiveryUpdateHandler implements IMessageHandler<LiveryUpdateMessage, IMessage>{

	public IMessage onMessage(LiveryUpdateMessage message, MessageContext ctx) {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId != message.dimId) return null;
		
		Entity target = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
		if (target == null) return null;
		
		CartLivery livery = (CartLivery) target.getExtendedProperties(CartLivery.EXT_PROP_NAME);
		if (livery == null) return null;

		livery.baseColor = message.livery.baseColor;
		livery.patternColor = message.livery.patternColor;
		livery.pattern = message.livery.pattern;
		return null;
	}

}
