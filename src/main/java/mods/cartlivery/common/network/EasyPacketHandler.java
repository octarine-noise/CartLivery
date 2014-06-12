package mods.cartlivery.common.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;

public class EasyPacketHandler implements IPacketHandler {

	protected Logger log;
	
	protected String channel;
	protected Map<Class<? extends EasyPacket>, Integer> classToId = Maps.newHashMap();
	protected Map<Integer, Class<? extends EasyPacket>> idToClass = Maps.newHashMap();
	
	protected EasyPacketHandler() {}
	
	public static EasyPacketHandler register(String channel, Logger log) {
		EasyPacketHandler handler = new EasyPacketHandler();
		handler.channel = channel;
		handler.log = log;
		NetworkRegistry.instance().registerChannel(handler, channel);
		return handler;
	}
	
	public void registerPacketType(int type, Class<? extends EasyPacket> packetClass) {
		classToId.put(packetClass, type);
		idToClass.put(type, packetClass);
	}
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(payload.data));
		try {
			// determine packet type
			int type = dis.readByte();
			Class<? extends EasyPacket> packetClass = idToClass.get(type);
			if (packetClass == null) {
				log.warning(String.format("Wrong packet type: channel %s, type %d", channel, type));
				return;
			}
			
			// create packet
			EasyPacket packet;
			try {
				packet = packetClass.newInstance();
			} catch (Exception e) {
				log.warning(String.format("Error instantiating packet: channel %s, class %s", channel, packetClass.getName()));
				return;
			}
			
			// read packet data
			try {
				packet.read(dis);
			} catch (Exception e) {
				log.warning(String.format("Error reading packet data: channel %s, class %s", channel, packetClass.getName()));
			}
			
			packet.receive(player);
			
		} catch (IOException e) {
			log.warning(String.format("Error reading packet: channel %s", channel));
		}
	}

	public void sendPacketData(EasyPacket packet) {
		Integer type = classToId.get(packet.getClass());
		if (type == null) {
			log.warning(String.format("Wrong packet type: channel %s, class %s", channel, packet.getClass().getName()));
			return;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeByte(type);
			packet.write(dos);
		} catch (IOException e) {
			log.warning(String.format("Error writing packet data: channel %s, class %s", channel, packet.getClass().getName()));
		}
		
		Packet250CustomPayload payload = new Packet250CustomPayload(channel, baos.toByteArray());
		packet.send(payload);
	}
}
