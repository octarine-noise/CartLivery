package mods.cartlivery.common.network;

import mods.cartlivery.ModCartLivery;
import mods.cartlivery.common.CartLivery;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LiveryRequestHandler implements IMessageHandler<LiveryRequestMessage, LiveryUpdateMessage> {

	public LiveryUpdateMessage onMessage(LiveryRequestMessage message, MessageContext ctx) {
		World world = DimensionManager.getWorld(message.dimId);
		Entity cart = world.getEntityByID(message.entityId);
		CartLivery livery = (CartLivery) cart.getExtendedProperties(CartLivery.EXT_PROP_NAME);
		if (livery == null) {
			ModCartLivery.log.warn(I18n.format("message.cartlivery.invalidLiveryRequest", ctx.getServerHandler().playerEntity.getDisplayName()));
			return null;
		}
		return new LiveryUpdateMessage(cart, livery);
	}

}
