package mods.cartlivery.common.network;

import mods.cartlivery.CommonProxy;
import mods.cartlivery.common.CartLivery;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LiveryRequestHandler implements IMessageHandler<LiveryRequestMessage, IMessage> {

	public IMessage onMessage(LiveryRequestMessage message, MessageContext ctx) {
		World world = DimensionManager.getWorld(message.dimId);
		if (world == null) return null;
		
		Entity cart = world.getEntityByID(message.entityId);
		if (cart == null) return null;
		
		CartLivery livery = (CartLivery) cart.getExtendedProperties(CartLivery.EXT_PROP_NAME);
		if (livery == null) return null;

		CommonProxy.network.sendTo(new LiveryUpdateMessage(cart, livery), ctx.getServerHandler().playerEntity);
		return null;
	}

}
