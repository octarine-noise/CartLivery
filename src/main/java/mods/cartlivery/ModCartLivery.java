package mods.cartlivery;


import java.io.File;
import java.util.logging.Logger;

import mods.cartlivery.client.LiveryTextureRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(modid=ModCartLivery.MOD_ID, name=ModCartLivery.MOD_NAME, useMetadata=true, dependencies="after:Railcraft")
@NetworkMod(channels={ModCartLivery.CHANNEL_NAME}, clientSideRequired=true, serverSideRequired=false)
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
		
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "cartlivery.cfg"));
		proxy.itemIdSticker = config.getItem("itemSticker", 3901).getInt();
		proxy.itemIdCutter = config.getItem("itemCutter", 3902).getInt();
		if (config.hasChanged()) config.save();
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
					log.info(String.format(I18n.getString("message.cartlivery.invalidExclusion"), message.getSender(), message.getStringValue()));
				}
			}
			if ("addBuiltInLiveries".equals(message.key) && message.isStringMessage() && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				String[] liveries = message.getStringValue().split(",");
				log.info(String.format(I18n.getString("message.cartlivery.registerBuiltIn"), liveries.length, message.getSender()));
				for(String livery : liveries) LiveryTextureRegistry.builtInLiveries.put(livery, message.getSender());
			}
		}
	}
	
}
