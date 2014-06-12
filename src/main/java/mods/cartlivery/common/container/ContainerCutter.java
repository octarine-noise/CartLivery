package mods.cartlivery.common.container;

import mods.cartlivery.common.item.ItemSticker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerCutter extends Container {

	public String pattern = "";
	public EntityPlayer player;
	public int toolIndex;
	public IInventory inventoryInput;
	public IInventory inventoryOutput;
	
	public ContainerCutter(EntityPlayer player) {
		this.player = player;
		toolIndex = player.inventory.currentItem;
		this.inventoryInput = new InventoryCrafting(this, 1, 1);
		this.inventoryOutput = new InventoryCraftResult();
		
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));

        for (int i = 0; i < 9; ++i)
            if (i != toolIndex) {
            	addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
            } else {
            	addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142) {
					@Override
					public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
						return false;
					}
            	});
            }
        
        addSlotToContainer(new Slot(inventoryInput, 0, 106, 36) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Item.paper;
			}
        });
        addSlotToContainer(new SlotCrafting(player, inventoryInput, inventoryOutput, 0, 144, 36) {
			@Override
			public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
				super.onPickupFromSlot(player, stack);
				damageTool();
			}
        });
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		ItemStack drop = inventoryInput.getStackInSlotOnClosing(0);
		if (drop != null) player.dropPlayerItemWithRandomChoice(drop, false);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inv) {
		if (inventoryInput.getStackInSlot(0) == null || pattern.isEmpty()) {
			inventoryOutput.setInventorySlotContents(0, null);
		} else {
			inventoryOutput.setInventorySlotContents(0, ItemSticker.create(pattern));			
		}
	}

	protected void damageTool() {
		ItemStack tool = player.inventory.getStackInSlot(toolIndex);
		tool.setItemDamage(tool.getItemDamage() + 1);
		if (tool.getItemDamage() > tool.getMaxDamage()) {
			player.inventory.setInventorySlotContents(toolIndex, null);
			player.closeScreen();
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
	
}
