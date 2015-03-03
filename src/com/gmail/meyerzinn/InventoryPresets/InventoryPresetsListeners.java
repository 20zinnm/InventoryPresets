package com.gmail.meyerzinn.InventoryPresets;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryPresetsListeners implements Listener {

	@EventHandler
	public void onSignChangeEvent(SignChangeEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("inventorypresets.sign")) {
			if (e.getLine(0).equalsIgnoreCase("[Presets]")) {
				if (e.getLine(1).equalsIgnoreCase("Save")) {
					e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA
							+ "Presets" + ChatColor.GRAY + "]");
					e.setLine(1, ChatColor.RED + "" + ChatColor.BOLD
							+ "Save Preset");
					e.setLine(2, ChatColor.GREEN + "Richt Click to");
					e.setLine(3, ChatColor.GREEN + "save a preset.");
					return;
				}
				if (e.getLine(1).equalsIgnoreCase("Load")) {
					e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA
							+ "Presets" + ChatColor.GRAY + "]");
					e.setLine(1, ChatColor.RED + "" + ChatColor.BOLD
							+ "Load Preset");
					e.setLine(2, ChatColor.GREEN + "Right Click to");
					e.setLine(3, ChatColor.GREEN + "load a preset.");
					return;
				}
				if (e.getLine(1).equalsIgnoreCase("Delete")) {
					e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA
							+ "Presets" + ChatColor.GRAY + "]");
					e.setLine(1, ChatColor.RED + "" + ChatColor.BOLD
							+ "Delete Preset");
					e.setLine(2, ChatColor.GREEN + "Right Click to");
					e.setLine(3, ChatColor.GREEN + "delete a preset.");
					return;
				}
			}
		}
	}

	@EventHandler
	public void SignEvent(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				Player p = e.getPlayer();
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase(
						ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Presets"
								+ ChatColor.GRAY + "]")) {
					if (InventoryPresets.promped.containsKey(p.getUniqueId()
							.toString())) {
						p.sendMessage(ChatColor.RED
								+ "You have not answered the previous prompt! Say cancel to exit the promt.");
						return;
					}
					if (sign.getLine(1)
							.equalsIgnoreCase(
									ChatColor.RED + "" + ChatColor.BOLD
											+ "Save Preset")) {
						InventoryPresets.promped.put(
								p.getUniqueId().toString(), "save");
						p.sendMessage(ChatColor.BLUE
								+ "What would you like to call your preset? (Say it in chat.)");
						return;
					}
					if (sign.getLine(1)
							.equalsIgnoreCase(
									ChatColor.RED + "" + ChatColor.BOLD
											+ "Load Preset")) {
						if (!InventoryPresets.presets.containsKey(p
								.getUniqueId().toString())) {
							p.sendMessage(ChatColor.RED
									+ "You have no saved presets!");
							return;
						}
						InventoryPresets.promped.put(
								p.getUniqueId().toString(), "load");
						HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
								.get(p.getUniqueId().toString());
						String list = "";
						p.sendMessage(ChatColor.BLUE
								+ "Which would you like to load? (Say it in chat.)");
						for (PlayerPreset i : CurrentPresets.values()) {
							list = list + " " + i.name;
						}
						list.replace(" ", ", ");
						p.sendMessage(ChatColor.BLUE + "Presets: " + list);
						return;
					}
					if (sign.getLine(1).equalsIgnoreCase(
							ChatColor.RED + "" + ChatColor.BOLD
									+ "Delete Preset")) {
						if (!InventoryPresets.presets.containsKey(p
								.getUniqueId().toString())) {
							p.sendMessage(ChatColor.RED
									+ "You have no saved presets!");
							return;
						}
						InventoryPresets.promped.put(
								p.getUniqueId().toString(), "delete");
						HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
								.get(p.getUniqueId().toString());
						String list = "Presets:";
						p.sendMessage(ChatColor.BLUE
								+ "Which would you like to delete? (Say it in chat.)");
						for (PlayerPreset i : CurrentPresets.values()) {
							list = list + " " + i.name;
						}
						list.replace(" ", ", ");
						p.sendMessage(ChatColor.BLUE + "Presets: " + list);
						return;
					}

				}
			}
		}
	}

	@EventHandler
	public void PromptHandler(AsyncPlayerChatEvent e) {
		if (InventoryPresets.promped.containsKey(e.getPlayer().getUniqueId()
				.toString())) {
			Player p = e.getPlayer();
			String name = e.getMessage();
			if (InventoryPresets.promped.get(p.getUniqueId().toString()) == "save") {
				PlayerPreset pp = new PlayerPreset();
				pp.invItems = p.getInventory().getContents();
				pp.invArmour = p.getInventory().getArmorContents();
				pp.name = name;
				HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
						.get((p.getUniqueId().toString()));
				if (CurrentPresets != null) {
					CurrentPresets.put(name, pp);
					InventoryPresets.presets.remove(p.getUniqueId().toString());
					InventoryPresets.presets.put(p.getUniqueId().toString(),
							CurrentPresets);
					p.sendMessage(ChatColor.BLUE + "Saved your preset, " + name);
					InventoryPresets.promped.remove(p.getUniqueId().toString());
					e.setCancelled(true);
					return;
				} else {
					HashMap<String, PlayerPreset> CurrentPresets2 = new HashMap<String, PlayerPreset>();
					CurrentPresets2.put(name, pp);
					InventoryPresets.presets.put(p.getUniqueId().toString(),
							CurrentPresets2);
					p.sendMessage(ChatColor.BLUE + "Saved your first preset, "
							+ name + ".");
					InventoryPresets.promped.remove(p.getUniqueId().toString());
					e.setCancelled(true);
					return;
				}
			}
			if (InventoryPresets.promped.get(p.getUniqueId().toString()) == "load") {
				if (!InventoryPresets.presets.containsKey(p.getUniqueId()
						.toString())) {
					p.sendMessage(ChatColor.RED + "You have no saved presets!");
					return;
				}
				HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
						.get(p.getUniqueId().toString());
				if (!CurrentPresets.containsKey(name)) {
					p.sendMessage(ChatColor.RED
							+ "There are no presets saved to you by that name!");
					return;
				}
				PlayerPreset preset = (PlayerPreset) CurrentPresets.get(name);
				p.getInventory().setContents((ItemStack[]) preset.invItems);
				p.getInventory().setArmorContents(
						(ItemStack[]) preset.invArmour);
				p.sendMessage(ChatColor.BLUE + "Equipped preset! Enjoy.");
				InventoryPresets.promped.remove(p.getUniqueId().toString());
				e.setCancelled(true);
				return;
			}
			if (InventoryPresets.promped.get(p.getUniqueId().toString()) == "delete") {
				if (!InventoryPresets.presets.containsKey(p.getUniqueId()
						.toString())) {
					p.sendMessage(ChatColor.RED + "You have no saved presets!");
					return;
				}
				HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
						.get(p.getUniqueId().toString());
				if (!CurrentPresets.containsKey(name)) {
					p.sendMessage(ChatColor.RED
							+ "There are no presets saved to you by that name!");
					return;
				}
				CurrentPresets.remove(name);
				InventoryPresets.presets.remove(p.getUniqueId().toString());
				InventoryPresets.presets.put(p.getUniqueId().toString(),
						CurrentPresets);
				p.sendMessage(ChatColor.BLUE + "Deleted preset. RIP " + name
						+ ".");
				InventoryPresets.promped.remove(p.getUniqueId().toString());
				e.setCancelled(true);
				return;
			}
		}
	}

}
