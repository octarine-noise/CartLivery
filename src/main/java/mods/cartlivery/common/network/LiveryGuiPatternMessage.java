package mods.cartlivery.common.network;

import mods.cartlivery.common.utils.NetworkUtil;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class LiveryGuiPatternMessage implements IMessage {

	String pattern;
	
	public LiveryGuiPatternMessage() { }
	
	public LiveryGuiPatternMessage(String pattern) {
		this.pattern = pattern;
	}
	
	public void fromBytes(ByteBuf buf) {
		pattern = NetworkUtil.readString(buf);
	}

	public void toBytes(ByteBuf buf) {
		NetworkUtil.writeString(pattern, buf);
	}

}
