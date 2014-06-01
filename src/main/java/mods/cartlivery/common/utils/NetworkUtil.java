package mods.cartlivery.common.utils;

import net.minecraft.entity.Entity;
import io.netty.buffer.ByteBuf;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class NetworkUtil {

	private NetworkUtil() { }
	
	public static void writeString(String string, ByteBuf buf) {
		if (string == null || string.isEmpty()) {
			buf.writeShort(0);
		} else {
			byte[] coded = string.getBytes(Charsets.UTF_8);
			buf.writeShort(coded.length);
			buf.writeBytes(coded);
		}
	}
	
	public static String readString(ByteBuf buf) {
		int length = buf.readShort();
		if (length == 0) return "";
		byte[] coded = new byte[length];
		buf.readBytes(coded);
		return new String(coded, Charsets.UTF_8);
	}
	
	public static TargetPoint targetEntity(Entity entity, int range) {
		return new TargetPoint(entity.worldObj.provider.dimensionId, entity.posX, entity.posY, entity.posZ, range);
	}
	
	public static TargetPoint targetEntity(Entity entity) {
		return targetEntity(entity, 256);
	}
}
