package mods.cartlivery.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.cartlivery.CommonProxy;
import mods.cartlivery.common.CartLivery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class LiveryRequestPacket extends EasyPacket {

	protected int dimId;
	protected int entityId;
	
	public LiveryRequestPacket() {}
	
	public LiveryRequestPacket(Entity entity) {
		dimId = entity.worldObj.provider.dimensionId;
		entityId = entity.entityId;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		dimId = dis.readInt();
		entityId = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(dimId);
		dos.writeInt(entityId);
	}
	
	@Override
	public void send(Packet packet) {
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	public void receive(Player player) {
		if (player != null && player instanceof EntityPlayer) {
			World world = DimensionManager.getWorld(dimId);
			if (world == null) return;
			
			Entity entity = world.getEntityByID(entityId);
			if (entity == null) return;
			
			if (entity instanceof EntityMinecart && entity.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
				CommonProxy.network.sendPacketData(new LiveryUpdatePacket(entity, (EntityPlayer) player));
			}
		}
		
	}
}
