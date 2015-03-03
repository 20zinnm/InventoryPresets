package com.gmail.meyerzinn.InventoryPresets;

import org.bukkit.inventory.ItemStack;

public class PlayerPreset implements java.io.Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	public ItemStack[] invItems;
	public ItemStack[] invArmour;
	public String name;

	public PlayerPreset() {

	}

}
