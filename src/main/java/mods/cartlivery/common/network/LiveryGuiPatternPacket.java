package mods.cartlivery.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.cartlivery.common.container.ContainerCutter;
import mods.cartlivery.common.utils.NetworkUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class LiveryGuiPatternPacket extends EasyPacket {

	protected String pattern;
	
	public LiveryGuiPatternPacket() {}
	
	public LiveryGuiPatternPacket(String pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		pattern = NetworkUtil.readString(dis);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		NetworkUtil.writeString(pattern, dos);
	}

	@Override
	public void send(Packet packet) {
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	public void receive(Player player) {
		if (player instanceof EntityPlayer) {
			Container container = ((EntityPlayer) player).openContainer;
			if (container != null && container instanceof ContainerCutter) {
				ContainerCutter cutter = (ContainerCutter) container;
				cutter.pattern = pattern;
				cutter.onCraftMatrixChanged(cutter.inventoryInput);
			}
		}
	}

}
