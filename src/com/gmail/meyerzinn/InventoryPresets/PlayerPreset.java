/*******************************************************
 * Copyright (C) 2015 Meyer Zinn meyerzinn@gmail.com
 * 
 * This file is part of InventoryPresets.
 * 
 * InventoryPresets can not be copied and/or distributed
 * without the express permission of Meyer Zinn.
 *******************************************************/

package com.gmail.meyerzinn.InventoryPresets;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("PlayerPreset")
public class PlayerPreset implements ConfigurationSerializable {

	private ItemStack[] invItems;

	public ItemStack[] getInvItems() {
		return invItems;
	}

	public void setInvItems(ItemStack[] invItems) {
		this.invItems = invItems;
	}

	private ItemStack[] invArmour;

	public ItemStack[] getInvArmour() {
		return invArmour;
	}

	public void setInvArmour(ItemStack[] invArmour) {
		this.invArmour = invArmour;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name;

	public PlayerPreset() {

	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> presetmap = new LinkedHashMap<String, Object>();

		presetmap.put("invItems", this.invItems);
		presetmap.put("invArmor", this.invArmour);
		presetmap.put("name", this.name);

		return presetmap;
	}

	public static PlayerPreset deserialize(HashMap<String, Object> presetmap) {
		if (!presetmap.containsKey("name"))
			return null;
		String name = (String) presetmap.get("name");
		ItemStack[] invItems = (ItemStack[]) presetmap.get("invItems");
		presetmap.get("invArmour");

		PlayerPreset presetfinal = new PlayerPreset();
		presetfinal.name = name;
		presetfinal.invItems = invItems;

		return presetfinal;
	}

}
