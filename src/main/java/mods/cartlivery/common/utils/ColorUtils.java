package mods.cartlivery.common.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ColorUtils {

	public static int getDyeColor(ItemStack dyeStack) {
		for (int idx = 0; idx < 16; idx++) {
			String oreName = new StringBuilder("dye").append(ItemDye.field_150923_a[idx]).toString().toLowerCase();
			for (int oreId : OreDictionary.getOreIDs(dyeStack)) {
				if (OreDictionary.getOreName(oreId).toLowerCase().startsWith(oreName)) return idx;
			}
		}
		return -1;
	}

	public static String getColorName(int color) {
		return (color < 0 || color > 15) ? "???" : I18n.format("color." + Integer.toString(color) + ".name");
	}
	
}
