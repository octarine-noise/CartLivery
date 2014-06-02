package mods.cartlivery;


import mods.cartlivery.client.LiveryTextureRegistry;
import net.minecraft.client.resources.I18n;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

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
			if ("addBuiltInLiveries".equals(message.key) && message.isStringMessage() && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				String[] liveries = message.getStringValue().split(",");
				log.info(I18n.format("message.cartlivery.registerBuiltIn", liveries.length, message.getSender()));
				for(String livery : liveries) LiveryTextureRegistry.builtInLiveries.put(livery, message.getSender());
			}
		}
	}
}
