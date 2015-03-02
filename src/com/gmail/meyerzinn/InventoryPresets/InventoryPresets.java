package com.gmail.meyerzinn.InventoryPresets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryPresets extends JavaPlugin {

	public static HashMap<String, HashMap<String, PlayerPreset>> presets = new HashMap<String, HashMap<String, PlayerPreset>>();

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

	public void onEnable() {
		File f = new File(getDataFolder() + File.separator + "presets.txt");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			getLogger().warning("Created new presets file.");
			getLogger()
					.warning(
							"DO NOT mess with the presets.txt file when the server is active. Please, don't touch it. If you change anything in the file and the plugin becomes unusable, you will receive no support other than simply deleting your presets file and letting it regenerate.");
		} else {
			presets = load(f);
			getLogger().info("Loaded Presets.");
		}
	}

	public void onDisable() {
		File f = new File(getDataFolder() + File.separator + "presets.txt");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			getLogger()
					.warning(
							"DO NOT mess with the presets.txt file when the server is active. Please, don't touch it. If you change anything in the file and the plugin becomes unusable, you will receive no support other than simply deleting your presets file and letting it regenerate.");
			save(presets, f.getAbsolutePath());
			getLogger().info("Saved Presets.");
		} else {
			save(presets, f.getAbsolutePath());
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
			pp.invItems = p.getInventory().getContents();
			pp.invArmour = p.getInventory().getArmorContents();
			pp.name = args[0];
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
			HashMap<String, PlayerPreset> CurrentPresets = presets.get(p.getUniqueId()
					.toString());
			if (args.length != 1) {
				String list = "Presets:";
				sender.sendMessage("Which would you like to load? /loadpreset [name]");
				for (PlayerPreset i : CurrentPresets.values()) {
					list = list + " " + i;
				}
				p.sendMessage(list);
				return true;
			}
			if (!CurrentPresets.containsKey(args[0])) {
				p.sendMessage(ChatColor.RED
						+ "There are no presets saved to you by that name!");
				return true;
			}
			PlayerPreset preset = (PlayerPreset) CurrentPresets.get(args[0]);
			p.getInventory().setContents((ItemStack[]) preset.invItems);
			p.getInventory().setArmorContents((ItemStack[]) preset.invArmour);
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
