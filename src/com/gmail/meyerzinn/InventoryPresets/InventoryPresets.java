/*******************************************************
 * Copyright (C) 2015 Meyer Zinn meyerzinn@gmail.com
 * 
 * This file is part of InventoryPresets.
 * 
 * InventoryPresets can not be copied and/or distributed
 * without the express permission of Meyer Zinn.
 *******************************************************/

package com.gmail.meyerzinn.InventoryPresets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryPresets extends JavaPlugin {

	public static HashMap<String, HashMap<String, PlayerPreset>> presets = new HashMap<String, HashMap<String, PlayerPreset>>();
	public static HashMap<String, String> promped = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	public static HashMap<String, HashMap<String, PlayerPreset>> load(File f) {
		try {
			@SuppressWarnings("resource")
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					f.getAbsolutePath()));
			Object result = ois.readObject();
			return (HashMap<String, HashMap<String, PlayerPreset>>) result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void save(HashMap<String, HashMap<String, PlayerPreset>> map,
			String path) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(path));
			oos.writeObject(map);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public HashMap<String, HashMap<String, PlayerPreset>> loadPresets(File f,
			File dataFolder) {
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			getLogger().warning("Created new presets file.");
			getLogger()
					.warning(
							"DO NOT mess with the presets.yml file when the server is active. Please, don't touch it. If you change anything in the file and the plugin becomes unusable, you will receive no support other than simply deleting your presets file and letting it regenerate.");
			return null;
		} else {

			HashMap<String, HashMap<String, PlayerPreset>> hash = new HashMap<String, HashMap<String, PlayerPreset>>();
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			for (String key : fc.getKeys(false)) {
				Map<String, PlayerPreset> m = new HashMap<String, PlayerPreset>();
				ConfigurationSection section = fc.getConfigurationSection(key);
				Map<String, Object> map = section.getValues(false);
				for (Entry<String, Object> e : map.entrySet()) {
					PlayerPreset pp = new PlayerPreset();
					pp.setName(e.getKey());
					ItemStack[] inventory = ((ArrayList<ItemStack>) fc.getList(
							key + "." + e.getKey() + "." + "invItems",
							new ArrayList<ItemStack>()))
							.toArray(new ItemStack[0]);
					pp.setInvItems(inventory);
					ItemStack[] inventorya = ((ArrayList<ItemStack>) fc
							.getList(
									key + "." + e.getKey() + "." + "invArmour",
									new ArrayList<ItemStack>()))
							.toArray(new ItemStack[0]);
					pp.setInvArmour(inventorya);
					m.put(pp.getName(), pp);
				}
				hash.put(key, (HashMap<String, PlayerPreset>) m);
			}
			return hash;
		}
	}

	public void onEnable() {
		ConfigurationSerialization.registerClass(PlayerPreset.class,
				"PlayerPreset");
		saveDefaultConfig();
		if (getConfig().getBoolean("auto-update")) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, 90032, getFile(),
					Updater.UpdateType.DEFAULT, true);
		}
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}
		File dataFolder = getDataFolder();
		File f = new File(getDataFolder(), "presets.yml");
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			getLogger().warning("Created new presets file.");
			getLogger()
					.warning(
							"DO NOT mess with the presets.yml file when the server is active. Please, don't touch it. If you change anything in the file and the plugin becomes unusable, you will receive no support other than simply deleting your presets file and letting it regenerate.");
		} else {
			HashMap<String, HashMap<String, PlayerPreset>> a = loadPresets(f,
					dataFolder);
			if (a != null) {
				presets = a;
				getLogger().info("Loaded presets!");
			} else {
				getLogger().severe("Whoops! There was an error loading!");
			}

		}
		getServer().getPluginManager().registerEvents(
				new InventoryPresetsListeners(), this);
	}

	public void onDisable() {
		File dataFolder = getDataFolder();
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		File f = new File(getDataFolder(), "presets.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			getLogger()
					.warning(
							"DO NOT mess with the presets.yml file when the server is active. Please, don't touch it. If you change anything in the file and the plugin becomes unusable, you will receive no support other than simply deleting your presets file and letting it regenerate.");
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			for (Entry<String, HashMap<String, PlayerPreset>> p : presets
					.entrySet()) {
				for (String s : p.getValue().keySet()) {
					PlayerPreset pp = p.getValue().get(s);
					Object o = pp.serialize();
					fc.set(p.getKey() + "." + pp.getName(), o);
				}
			}
			try {
				fc.save(f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getLogger().info("Saved Presets.");
		} else {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			for (Entry<String, HashMap<String, PlayerPreset>> p : presets
					.entrySet()) {
				for (String s : p.getValue().keySet()) {
					PlayerPreset pp = p.getValue().get(s);
					Object o = pp.serialize();
					fc.set(p.getKey() + "." + pp.getName(), o);
				}
			}
			try {
				fc.save(f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getLogger().info("Saved Presets.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Whoops! You can't use that! You need to be a player :P");
			return true;
		}
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED
					+ "Whoops! This command does not require more than 1 argument.");
			return true;
		}
		Player p = (Player) sender;
		if (cmd.getLabel().equalsIgnoreCase("savepreset")) {
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED
						+ "Whoops! Please use like '/savepreset name'. Saving as a name that already exists for you will override it.");
				return true;
			}
			PlayerPreset pp = new PlayerPreset();
			pp.setInvItems(p.getInventory().getContents());
			pp.setInvArmour(p.getInventory().getArmorContents());
			pp.setName(args[0]);
			HashMap<String, PlayerPreset> CurrentPresets = presets.get((p
					.getUniqueId().toString()));
			if (CurrentPresets != null) {
				CurrentPresets.put(args[0], pp);
				presets.remove(p.getUniqueId().toString());
				presets.put(p.getUniqueId().toString(), CurrentPresets);
				p.sendMessage(ChatColor.BLUE + "Saved preset " + args[0]);
				return true;
			} else {
				HashMap<String, PlayerPreset> CurrentPresets2 = new HashMap<String, PlayerPreset>();
				CurrentPresets2.put(args[0], pp);
				presets.put(p.getUniqueId().toString(), CurrentPresets2);
				p.sendMessage(ChatColor.BLUE + "Saved preset " + args[0]);
				return true;
			}
		}
		if (cmd.getLabel().equalsIgnoreCase("loadpreset")) {
			if (!presets.containsKey(p.getUniqueId().toString())) {
				p.sendMessage(ChatColor.RED + "You have no saved presets!");
				return true;
			}
			HashMap<String, PlayerPreset> CurrentPresets = presets.get(p
					.getUniqueId().toString());
			if (args.length != 1) {
				String list = "Presets:";
				sender.sendMessage("Which would you like to load? /loadpreset [name]");
				for (PlayerPreset i : CurrentPresets.values()) {
					list = list + " " + i.getName() + ",";
				}
				list.replace(" ", ", ");
				p.sendMessage(ChatColor.BLUE + "Presets: " + list);
				return true;
			}
			if (!CurrentPresets.containsKey(args[0])) {
				p.sendMessage(ChatColor.RED
						+ "There are no presets saved to you by that name!");
				return true;
			}
			PlayerPreset preset = (PlayerPreset) CurrentPresets.get(args[0]);
			p.getInventory().setContents((ItemStack[]) preset.getInvItems());
			p.getInventory().setArmorContents(
					(ItemStack[]) preset.getInvArmour());
			p.sendMessage(ChatColor.BLUE + "Equipped preset! Enjoy.");
			return true;
		}
		if (cmd.getLabel().equalsIgnoreCase("deletepreset")) {
			if (!presets.containsKey(p.getUniqueId().toString())) {
				p.sendMessage(ChatColor.RED + "You have no saved presets!");
				return true;
			}
			HashMap<String, PlayerPreset> CurrentPresets = presets.get(p
					.getUniqueId().toString());
			if (!CurrentPresets.containsKey(args[0])) {
				p.sendMessage(ChatColor.RED
						+ "There are no presets saved to you by that name!");
				return true;
			}
			CurrentPresets.remove(args[0]);
			presets.remove(p.getUniqueId().toString());
			presets.put(p.getUniqueId().toString(), CurrentPresets);
			return true;
		}
		return false;

	}

}
