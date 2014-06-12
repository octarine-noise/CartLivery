package mods.cartlivery;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import mods.cartlivery.client.LiveryTextureRegistry;
import mods.cartlivery.client.model.ModelCartLivery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.Restriction;

public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		super.init();
		
		((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new LiveryTextureRegistry());
		
		ModCartLivery.log.info(I18n.getString("message.cartlivery.overwriteModel"));
		if (Loader.isModLoaded("Railcraft")) {
			replaceRailcraftCartModel();	
		} else {
			replaceMinecraftCartModel();
		}
		
		if (Loader.isModLoaded("Waila")) {
			if (isWailaVersionSupported()) {
				FMLInterModComms.sendMessage("Waila", "register", "mods.cartlivery.integration.waila.CartLiveryWailaModule.register");
			} else {
				ModCartLivery.log.warning(I18n.getString("message.cartlivery.unacceptableWailaVersion"));
			}
		}
	}
	
	private void replaceMinecraftCartModel() {
		try {
			Field modelMinecart = null;
			for (Field field : RenderMinecart.class.getDeclaredFields()) {
				if (ModelBase.class.equals(field.getType())) {
					modelMinecart = field;
					break;
				}
			}
			modelMinecart.setAccessible(true);
			for (Class<?> entityClass : ImmutableList.<Class<?>>of(EntityMinecart.class, EntityMinecartTNT.class, EntityMinecartMobSpawner.class)) {
				RenderMinecart renderer = (RenderMinecart) RenderManager.instance.entityRenderMap.get(entityClass);
				modelMinecart.set(renderer, new ModelCartLivery());
			}
		} catch (Exception e) {
			ModCartLivery.log.warning(I18n.getString("message.cartlivery.overwriteModelMinecraftFail"));
		}
	}
	
	private void replaceRailcraftCartModel() {
		try {
			Class<?> modelManagerClass = Class.forName("mods.railcraft.client.render.carts.CartModelManager");
			Field defaultCore = modelManagerClass.getDeclaredField("modelMinecart");
			Field modifiers = Field.class.getDeclaredField("modifiers");
			defaultCore.setAccessible(true);
			modifiers.setAccessible(true);
			
			modifiers.set(defaultCore, defaultCore.getModifiers() & ~Modifier.FINAL);
			defaultCore.set(null, new ModelCartLivery());
			modifiers.set(defaultCore, defaultCore.getModifiers() | Modifier.FINAL);
		} catch (Exception e) {
			ModCartLivery.log.warning(I18n.getString("message.cartlivery.overwriteModelRailcraftFail"));
		}
	}
	
	public static boolean isWailaVersionSupported() {
		Restriction acceptedVersions = new Restriction(new DefaultArtifactVersion("1.5"), true, null, false);
		for (ModContainer mod : Loader.instance().getActiveModList()) {
			if ("Waila".equals(mod.getName())) return acceptedVersions.containsVersion(mod.getProcessedVersion()); 
		}
		return false;
	}
}
