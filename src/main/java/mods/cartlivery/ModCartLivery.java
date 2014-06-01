package mods.cartlivery;


import net.minecraft.client.resources.I18n;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=ModCartLivery.MOD_ID, name=ModCartLivery.MOD_NAME, useMetadata=true, dependencies="required-after:Forge@[10.12.1.1093,);after:Railcraft")
public class ModCartLivery {

	public static final String MOD_ID = "CartLivery";
	public static final String MOD_NAME = "Cart Livery";
	public static final String CHANNEL_NAME = "cartLiv";
	public static final String COMMON_PROXY_NAME = "mods.cartlivery.CommonProxy";
	public static final String CLIENT_PROXY_NAME = "mods.cartlivery.ClientProxy";

	@Mod.Instance
	public static ModCartLivery instance;
	
	@SidedProxy(serverSide=ModCartLivery.COMMON_PROXY_NAME, clientSide=ModCartLivery.CLIENT_PROXY_NAME)
	public static CommonProxy proxy;
	
	public static Logger log;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = event.getModLog();
	}
	
	@Mod.EventHandler
	public void initialize(FMLInitializationEvent event) {
		proxy.init();
		FMLInterModComms.sendMessage(MOD_ID, "addClassExclusion", "mods.railcraft.common.carts.EntityLocomotive");
		FMLInterModComms.sendMessage(MOD_ID, "addClassExclusion", "mods.railcraft.common.carts.EntityTunnelBore");
	}
	
	@Mod.EventHandler
	public void handleIMCMessage(FMLInterModComms.IMCEvent event) {
		for (IMCMessage message : event.getMessages()) {
			if ("addClassExclusion".equals(message.key) && message.isStringMessage()) {
				try {
					Class<?> clazz = Class.forName(message.getStringValue());
					CommonProxy.excludedClasses.add(clazz);
				} catch (ClassNotFoundException e) {
					I18n.format("message.cartlivery.invalidExclusion", message.getSender(), message.getStringValue());
				}
			}
		}
	}
}
