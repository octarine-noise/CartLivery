package mods.cartlivery;

import java.util.Set;

import mods.cartlivery.common.CartLivery;
import mods.cartlivery.common.item.ItemCutter;
import mods.cartlivery.common.item.ItemSticker;
import mods.cartlivery.common.item.LiveryStickerColoringRecipe;
import mods.cartlivery.common.network.EasyPacketHandler;
import mods.cartlivery.common.network.LiveryGuiPatternPacket;
import mods.cartlivery.common.network.LiveryRequestPacket;
import mods.cartlivery.common.network.LiveryUpdatePacket;
import mods.cartlivery.common.utils.ColorUtils;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

	public static EasyPacketHandler network = EasyPacketHandler.register(ModCartLivery.CHANNEL_NAME, ModCartLivery.log);
	public static Set<Class<?>> excludedClasses = Sets.newHashSet();
	
	protected int itemIdSticker;
	protected int itemIdCutter;
	public ItemSticker itemSticker;
	public ItemCutter itemCutter;

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		NetworkRegistry.instance().registerGuiHandler(ModCartLivery.instance, new GuiHandler());
		network.registerPacketType(0, LiveryRequestPacket.class);
		network.registerPacketType(1, LiveryUpdatePacket.class);
		network.registerPacketType(2, LiveryGuiPatternPacket.class);
		
		itemCutter = new ItemCutter(itemIdCutter);
		itemSticker = new ItemSticker(itemIdSticker);
		GameRegistry.registerItem(itemCutter, "cutter");
		GameRegistry.registerItem(itemSticker, "sticker");
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemCutter), Item.shears, Item.paper);
		GameRegistry.addRecipe(new LiveryStickerColoringRecipe());
		RecipeSorter.register("cartlivery:coloring", LiveryStickerColoringRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
		
		FMLInterModComms.sendMessage(ModCartLivery.MOD_ID, "addClassExclusion", "mods.railcraft.common.carts.EntityLocomotive");
		FMLInterModComms.sendMessage(ModCartLivery.MOD_ID, "addClassExclusion", "mods.railcraft.common.carts.EntityTunnelBore");
		FMLInterModComms.sendMessage(ModCartLivery.MOD_ID, "addBuiltInLiveries", "stripe1,stripe2,arrowup,dblarrow,corners1,bottom,thissideup,love,db,railtech,fragile");
	}
	
	@ForgeSubscribe
	public void handleMinecartConstruct(EntityConstructing event) {
		if (event.entity instanceof EntityMinecart) {
			for (Class<?> excluded : excludedClasses) if (excluded.isInstance(event.entity)) return;
			event.entity.registerExtendedProperties(CartLivery.EXT_PROP_NAME, new CartLivery());
		}
	}
	
	@ForgeSubscribe
	public void handleMinecartJoinWorld(EntityJoinWorldEvent event) {
		if (!event.entity.worldObj.isRemote) return;
		
		if (event.entity instanceof EntityMinecart && event.entity.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			CommonProxy.network.sendPacketData(new LiveryRequestPacket(event.entity));
		}
	}
	
	@ForgeSubscribe
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
				
				CommonProxy.network.sendPacketData(new LiveryUpdatePacket(event.target));
				event.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe
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
			
			CommonProxy.network.sendPacketData(new LiveryUpdatePacket(event.target));
			event.setCanceled(true);
		}
	}
	
	@ForgeSubscribe
	public void handleMinecartStickerRemove(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return;

		if (event.entityPlayer.isSneaking() && event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME) != null) {
			ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
			if (stack == null || !(stack.getItem() instanceof ItemCutter)) return;
			
			CartLivery livery = (CartLivery) event.target.getExtendedProperties(CartLivery.EXT_PROP_NAME);
			livery.pattern = "";
			
			CommonProxy.network.sendPacketData(new LiveryUpdatePacket(event.target));
			event.setCanceled(true);
		}
	}
	
}
