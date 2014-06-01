package mods.cartlivery.common.network;

import mods.cartlivery.ModCartLivery;
import mods.cartlivery.common.CartLivery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LiveryUpdateHandler implements IMessageHandler<LiveryUpdateMessage, NopMessage>{

	public NopMessage onMessage(LiveryUpdateMessage message, MessageContext ctx) {
		Entity target = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
		if (target == null) return null;
		CartLivery livery = (CartLivery) target.getExtendedProperties(CartLivery.EXT_PROP_NAME);
		if (livery == null) {
			ModCartLivery.log.warn(I18n.format("message.cartlivery.invalidLiveryUpdate"));
			return null;
		}
		livery.baseColor = message.livery.baseColor;
		livery.patternColor = message.livery.patternColor;
		livery.pattern = message.livery.pattern;
		return null;
	}

}
