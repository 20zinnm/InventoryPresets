package com.gmail.meyerzinn.InventoryPresets;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
					e.setLine(2, ChatColor.GREEN + "Richt Click -");
					e.setLine(3, ChatColor.GREEN + "save a preset.");
					return;
				}
				if (e.getLine(1).equalsIgnoreCase("Load")) {
					e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA
							+ "Presets" + ChatColor.GRAY + "]");
					e.setLine(1, ChatColor.RED + "" + ChatColor.BOLD
							+ "Load Preset");
					e.setLine(2, ChatColor.GREEN + "Right Click -");
					e.setLine(3, ChatColor.GREEN + "load a preset.");
					return;
				}
				if (e.getLine(1).equalsIgnoreCase("Delete")) {
					e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA
							+ "Presets" + ChatColor.GRAY + "]");
					e.setLine(1, ChatColor.RED + "" + ChatColor.BOLD
							+ "Delete Preset");
					e.setLine(2, ChatColor.GREEN + "Right Click -");
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
							list = list + " " + i.getName();
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
							list = list + " " + i.getName();
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
			if (e.getMessage().equalsIgnoreCase("cancel")) {
				InventoryPresets.promped.remove(p.getUniqueId().toString());
				p.sendMessage(ChatColor.RED + "Canceled!");
				e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			if (InventoryPresets.promped.get(p.getUniqueId().toString()) == "save") {
				PlayerPreset pp = new PlayerPreset();
				pp.setInvItems(p.getInventory().getContents());
				pp.setInvArmour(p.getInventory().getArmorContents());
				pp.setName(name);
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
					e.setCancelled(true);
					return;
				}
				HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
						.get(p.getUniqueId().toString());
				if (!CurrentPresets.containsKey(name)) {
					p.sendMessage(ChatColor.RED
							+ "There are no presets saved to you by that name!");
					e.setCancelled(true);
					return;
				}
				PlayerPreset preset = (PlayerPreset) CurrentPresets.get(name);
				p.getInventory().setContents((ItemStack[]) preset.getInvItems());
				p.getInventory().setArmorContents(
						(ItemStack[]) preset.getInvArmour());
				p.sendMessage(ChatColor.BLUE + "Equipped preset! Enjoy.");
				InventoryPresets.promped.remove(p.getUniqueId().toString());
				e.setCancelled(true);
				return;
			}
			if (InventoryPresets.promped.get(p.getUniqueId().toString()) == "delete") {
				if (!InventoryPresets.presets.containsKey(p.getUniqueId()
						.toString())) {
					p.sendMessage(ChatColor.RED + "You have no saved presets!");
					e.setCancelled(true);
					return;
				}
				HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets
						.get(p.getUniqueId().toString());
				if (!CurrentPresets.containsKey(name)) {
					p.sendMessage(ChatColor.RED
							+ "There are no presets saved to you by that name!");
					e.setCancelled(true);
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
			HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets.get((p
					.getUniqueId().toString()));
			if (CurrentPresets != null) {
				CurrentPresets.put(args[0], pp);
				InventoryPresets.presets.remove(p.getUniqueId().toString());
				InventoryPresets.presets.put(p.getUniqueId().toString(), CurrentPresets);
				p.sendMessage(ChatColor.BLUE + "Saved preset " + args[0]);
				return true;
			} else {
				HashMap<String, PlayerPreset> CurrentPresets2 = new HashMap<String, PlayerPreset>();
				CurrentPresets2.put(args[0], pp);
				InventoryPresets.presets.put(p.getUniqueId().toString(), CurrentPresets2);
				p.sendMessage(ChatColor.BLUE + "Saved preset " + args[0]);
				return true;
			}
		}
		if (cmd.getLabel().equalsIgnoreCase("loadpreset")) {
			if (!InventoryPresets.presets.containsKey(p.getUniqueId().toString())) {
				p.sendMessage(ChatColor.RED + "You have no saved presets!");
				return true;
			}
			HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets.get(p
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
			if (!InventoryPresets.presets.containsKey(p.getUniqueId().toString())) {
				p.sendMessage(ChatColor.RED + "You have no saved presets!");
				return true;
			}
			HashMap<String, PlayerPreset> CurrentPresets = InventoryPresets.presets.get(p
					.getUniqueId().toString());
			if (!CurrentPresets.containsKey(args[0])) {
				p.sendMessage(ChatColor.RED
						+ "There are no presets saved to you by that name!");
				return true;
			}
			CurrentPresets.remove(args[0]);
			InventoryPresets.presets.remove(p.getUniqueId().toString());
			InventoryPresets.presets.put(p.getUniqueId().toString(), CurrentPresets);
			return true;
		}
		return false;

	}
	
}
