package mods.cartlivery.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class LiveryRequestMessage implements IMessage {

	public int dimId;
	public int entityId;
	
	public LiveryRequestMessage() { }
	
	public LiveryRequestMessage(Entity cart) {
		dimId = cart.worldObj.provider.dimensionId;
		entityId = cart.getEntityId();
	}

	public void fromBytes(ByteBuf buf) {
		dimId = buf.readInt();
		entityId = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimId);
		buf.writeInt(entityId);
	}
	
	
}
