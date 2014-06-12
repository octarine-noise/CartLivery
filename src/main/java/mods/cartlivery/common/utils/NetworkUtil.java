package mods.cartlivery.common.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.base.Charsets;

public class NetworkUtil {

	private NetworkUtil() { }
	
	public static void writeString(String string, DataOutputStream dos) throws IOException {
		if (string == null || string.isEmpty()) {
			dos.writeShort(0);
		} else {
			byte[] coded = string.getBytes(Charsets.UTF_8);
			dos.writeShort(coded.length);
			dos.write(coded);
		}
	}
	
	public static String readString(DataInputStream dis) throws IOException {
		int length = dis.readShort();
		if (length == 0) return "";
		byte[] coded = new byte[length];
		dis.read(coded);
		return new String(coded, Charsets.UTF_8);
	}
	
}
