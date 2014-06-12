package mods.cartlivery.common.item;

import java.util.List;

import mods.cartlivery.ModCartLivery;
import mods.cartlivery.client.LiveryTextureInfo;
import mods.cartlivery.client.LiveryTextureRegistry;
import mods.cartlivery.common.utils.ColorUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSticker extends Item {

	public ItemSticker(int id) {
		super(id);
		setTextureName("paper");
		setUnlocalizedName("cartlivery.sticker");
		setMaxStackSize(4);
		setHasSubtypes(true);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativeTab, List list) {
		for (String pattern : LiveryTextureRegistry.getAvailableLiveries()) list.add(create(pattern));
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedInfo) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) return;
		LiveryTextureInfo info = LiveryTextureRegistry.map.get(tag.getString("pattern"));
		
		if (info == null) {
			list.add(String.format(I18n.getString("cartlivery.unknown"), tag.getString("pattern")));
		} else {
			list.add(I18n.getString("cartlivery." + tag.getString("pattern") + ".name"));
		}
		list.add(ColorUtils.getColorName(tag.getInteger("patternColor")));
	}

	public static ItemStack create(String pattern, int color) {
		ItemStack result = new ItemStack(ModCartLivery.proxy.itemSticker);
		NBTTagCompound tag = new NBTTagCompound();
		result.setTagCompound(tag);
		tag.setString("pattern", pattern);
		tag.setInteger("patternColor", color);
		return result;
	}
	
	public static ItemStack create(String pattern) {
		return create(pattern, 15);
	}
}
