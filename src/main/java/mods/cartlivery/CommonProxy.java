package mods.cartlivery;

import java.util.Set;

import mods.cartlivery.common.CartLivery;
import mods.cartlivery.common.item.ItemCutter;
import mods.cartlivery.common.item.ItemSticker;
import mods.cartlivery.common.item.LiveryStickerColoringRecipe;
import mods.cartlivery.common.network.LiveryGuiPatternHandler;
import mods.cartlivery.common.network.LiveryGuiPatternMessage;
import mods.cartlivery.common.network.LiveryRequestHandler;
import mods.cartlivery.common.network.LiveryRequestMessage;
import mods.cartlivery.common.network.LiveryUpdateHandler;
import mods.cartlivery.common.network.LiveryUpdateMessage;
import mods.cartlivery.common.utils.ColorUtils;
import mods.cartlivery.common.utils.NetworkUtil;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	public static SimpleNetworkWrapper network = new SimpleNetworkWrapper(ModCartLivery.CHANNEL_NAME);
	public static Set<Class<?>> excludedClasses = Sets.newHashSet();
	
	public static ItemSticker itemSticker = new ItemSticker();
	public static ItemCutter itemCutter = new ItemCutter();

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		NetworkRegistry.INSTANCE.registerGuiHandler(ModCartLivery.instance, new GuiHandler());
		network.registerMessage(LiveryRequestHandler.class, LiveryRequestMessage.class, 0, Side.SERVER);
		network.registerMessage(LiveryUpdateHandler.class, LiveryUpdateMessage.class, 1, Side.CLIENT);
		network.registerMessage(LiveryGuiPatternHandler.class, LiveryGuiPatternMessage.class, 2, Side.SERVER);
		
		GameRegistry.registerItem(itemCutter, "cutter");
		GameRegistry.registerItem(itemSticker, "sticker");
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemCutter), Items.shears, Items.paper);
		GameRegistry.addRecipe(new LiveryStickerColoringRecipe());
		RecipeSorter.register("cartlivery:coloring", LiveryStickerColoringRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
		
		FMLInterModComms.sendMessage(ModCartLivery.MOD_ID, "addClassExclusion", "mods.railcraft.common.carts.EntityLocomotive");
		FMLInterModComms.sendMessage(ModCartLivery.MOD_ID, "addClassExclusion", "mods.railcraft.common.carts.EntityTunnelBore");
		FMLInterModComms.sendMessage(ModCartLivery.MOD_ID, "addBuiltInLiveries", "stripe1,stripe2,arrowup,dblarrow,corners1,bottom,thissideup,love,db,railtech,fragile");
	}
	
	@SubscribeEvent
	public void handleMinecartConstruct(EntityConstructing event) {
		if (event.entity instanceof EntityMinecart) {
			for (Class<?> excluded : excludedClasses) if (excluded.isInstance(event.entity)) return;
			event.entity.registerExtendedProperties(CartLivery.EXT_PROP_NAME, new CartLivery());
		}
	}
	
	@SubscribeEvent
	public void handleMinecartJoinWorld(EntityJoinWorldEvent event) {
		if (!event.entity.worldObj.isRemote) return;
		
		if (event.entity instanceof EntityMinecart && event.entity.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			CommonProxy.network.sendToServer(new LiveryRequestMessage(event.entity));
		}
	}
	
	@SubscribeEvent
	public void handleMinecartPainting(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return;
		
		if (event.entityPlayer.isSneaking() && event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
			CartLivery livery = (CartLivery) event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME);
			int newColor = ColorUtils.getDyeColor(stack);	
			
			if (newColor != -1 && (!livery.pattern.isEmpty() || newColor != livery.baseColor)) {
				livery.baseColor = newColor;
				livery.pattern = "";
				stack.stackSize--;
				if (stack.stackSize == 0) event.entityPlayer.setCurrentItemOrArmor(0, null);
				
				CommonProxy.network.sendToAllAround(new LiveryUpdateMessage(event.target, livery), NetworkUtil.targetEntity(event.target));
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void handleMinecartStickerApply(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return;
		
		if (event.entityPlayer.isSneaking() && event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
			if (stack == null || !(stack.getItem() instanceof ItemSticker) || stack.getTagCompound() == null) return;
			
			String pattern = stack.getTagCompound().getString("pattern");
			int patternColor = stack.getTagCompound().getInteger("patternColor");
			if (pattern.isEmpty()) return;
			
			CartLivery livery = (CartLivery) event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME);
			if (!livery.pattern.isEmpty()) return;
			
			livery.pattern = pattern;
			livery.patternColor = patternColor;
			
			stack.stackSize--;
			if (stack.stackSize == 0) event.entityPlayer.setCurrentItemOrArmor(0, null);
			
			CommonProxy.network.sendToAllAround(new LiveryUpdateMessage(event.target, livery), NetworkUtil.targetEntity(event.target));
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void handleMinecartStickerRemove(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return;

		if (event.entityPlayer.isSneaking() && event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
			if (stack == null || !(stack.getItem() instanceof ItemCutter)) return;
			
			CartLivery livery = (CartLivery) event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME);
			livery.pattern = "";
			
			CommonProxy.network.sendToAllAround(new LiveryUpdateMessage(event.target, livery), NetworkUtil.targetEntity(event.target));
			event.setCanceled(true);
		}
	}
	
}
