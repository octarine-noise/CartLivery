package mods.cartlivery.common.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ColorUtils {

	public static final String[] dyeOreNames = new String[] {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};
	
	public static int getDyeColor(ItemStack dyeStack) {
		for (int idx = 0; idx < 16; idx++) {
			for (ItemStack dyeTarget : OreDictionary.getOres(dyeOreNames[idx])) {
				if (OreDictionary.itemMatches(dyeTarget, dyeStack, false)) return idx;
			}
		}
		return -1;
	}

	public static String getColorName(int color) {
		return (color < 0 || color > 15) ? "???" : I18n.getString("color." + Integer.toString(color) + ".name");
	}
	
}
