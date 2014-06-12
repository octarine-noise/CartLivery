package mods.cartlivery.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import mods.cartlivery.common.CartLivery;
import mods.cartlivery.common.utils.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

public class LiveryUpdatePacket extends EasyPacket {

	protected int dimId;
	protected int entityId;
	protected EntityPlayer target;
	
	protected int baseColor;
	protected int patternColor;
	protected String pattern = "";
	
	protected int sendRange = 192;
	protected double posX, posY, posZ;
	
	public LiveryUpdatePacket() {}
	
	public LiveryUpdatePacket(Entity entity) {
		dimId = entity.worldObj.provider.dimensionId;
		entityId = entity.entityId;
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
		
		if (entity.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			CartLivery livery = (CartLivery) entity.getExtendedProperties(CartLivery.EXT_PROP_NAME);
			baseColor = livery.baseColor;
			patternColor = livery.patternColor;
			pattern = livery.pattern;
		}
	}
	
	public LiveryUpdatePacket(Entity entity, EntityPlayer target) {
		this(entity);
		this.target = target;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		dimId = dis.readInt();
		entityId = dis.readInt();
		baseColor = dis.readShort();
		patternColor = dis.readShort();
		pattern = NetworkUtil.readString(dis);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(dimId);
		dos.writeInt(entityId);
		dos.writeShort(baseColor);
		dos.writeShort(patternColor);
		NetworkUtil.writeString(pattern, dos);
	}

	@Override
	public void send(Packet packet) {
		if (target != null) {
			PacketDispatcher.sendPacketToPlayer(packet, (Player) target);
		} else {
			PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, sendRange, dimId, packet);
		}
	}

	@Override
	public void receive(Player player) {
		if (dimId != Minecraft.getMinecraft().theWorld.provider.dimensionId) return;
		
		Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
		if (entity == null) return;
		
		if (entity instanceof EntityMinecart && entity.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			CartLivery livery = (CartLivery) entity.getExtendedProperties(CartLivery.EXT_PROP_NAME);
			livery.baseColor = baseColor;
			livery.patternColor = patternColor;
			livery.pattern = pattern;
		}
	}
}
