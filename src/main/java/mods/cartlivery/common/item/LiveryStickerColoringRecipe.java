package mods.cartlivery.common.item;

import mods.cartlivery.common.utils.ColorUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class LiveryStickerColoringRecipe implements IRecipe {

	ItemStack sample = ItemSticker.create("");
	
	public boolean matches(InventoryCrafting inv, World world) {
		int numSticker = 0;
		int numDye = 0;
		for (int idx = 0; idx < inv.getSizeInventory(); idx++) {
			ItemStack stack = inv.getStackInSlot(idx);
			if (stack == null) continue;
			
			if (stack.getItem() instanceof ItemSticker && stack.getTagCompound() != null) {
				numSticker++;
			} else if (ColorUtils.getDyeColor(stack) != -1) {
				numDye++;
			} else {
				return false;
			}
		}
		return numSticker == 1 && numDye == 1;
	}

	public ItemStack getCraftingResult(InventoryCrafting inv) {
		String pattern = null;
		int dyeColor = 0;
		for (int idx = 0; idx < inv.getSizeInventory(); idx++) {
			ItemStack stack = inv.getStackInSlot(idx);
			if (stack == null) continue;
			
			if (stack.getItem() instanceof ItemSticker && stack.getTagCompound() != null) {
				pattern = stack.getTagCompound().getString("pattern");
			} else if (ColorUtils.getDyeColor(stack) != -1) {
				dyeColor = ColorUtils.getDyeColor(stack);
			} else {
				return null;
			}
		}
		return ItemSticker.create(pattern, dyeColor);
	}

	public int getRecipeSize() {
		return 2;
	}

	public ItemStack getRecipeOutput() {
		return sample;
	}

}
