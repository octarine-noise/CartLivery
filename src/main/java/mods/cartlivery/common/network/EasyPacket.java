package mods.cartlivery.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.Player;

import net.minecraft.network.packet.Packet;

public abstract class EasyPacket {

	public abstract void read(DataInputStream dis) throws IOException;
	public abstract void write(DataOutputStream dos) throws IOException;
	public abstract void send(Packet packet);
	public abstract void receive(Player player);
}
