package me.ipodtouch0218.mobheadsplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.ipodtouch0218.mobheadsplus.dataholder.EntityData;
import me.ipodtouch0218.mobheadsplus.externallibs.Skull;

public class CommandSpawnHead implements CommandExecutor, TabCompleter {

	private MobHeadsPlus instance;
	
	public CommandSpawnHead(MobHeadsPlus instance) {
		this.instance = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("MobHeadsPlus> Invalid Usage: Only players may use this command!");
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage("MobHeadsPlus> Invalid Usage: /spawnhead <mob> [data]");
			return true;
		}
		
		EntityType headType;
		try {
			headType = EntityType.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			sender.sendMessage("MobHeadsPlus> Invalid Argument: \"" + args[0] + "\" is not a valid entity type!");
			return true;
		}
		String headData = args.length >= 2 ? (args[1].equalsIgnoreCase("none") ? null : args[1]) : null;
		EntityData entityData = instance.getEntityData(headType, headData);
		if (headType == EntityType.PLAYER) {
			entityData = instance.getEntityData(headType, null);
		}
		if (entityData == null || (headType == EntityType.PLAYER && entityData == null)) {
			sender.sendMessage("MobHeadsPlus> Invalid Argument: Could not find entity with provided data!");
			return true;
		}
		
		Player playerSender = (Player) sender;
		if (headType == EntityType.PLAYER) {
			ItemStack skull = Skull.getPlayerSkull(headData);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setDisplayName(entityData.getSkull().clone().getItemMeta().getDisplayName().replace("{0}", headData));
			skull.setItemMeta(meta);
			
			playerSender.getInventory().addItem(skull);
		} else {
			playerSender.getInventory().addItem(entityData.getSkull());
		}
		sender.sendMessage("MobHeadsPlus> Success: A " + headType.name() + " skull was added to your Inventory!");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> completes = new ArrayList<String>();
		if (args.length == 1) { //entity type completion
			for (EntityData data : instance.getAllEntityData()) {
				String type = data.getType().name();
				
				if (!args[0].equals("") || !args[0].equals(" ")) {
					if (!type.toUpperCase().startsWith(args[0].toUpperCase())) { 
						continue; 
					}
				}
				
				if (!completes.contains(type)) {
					completes.add(type);
				}
			}
		} else if (args.length == 2) { //data completion
			for (EntityData data : instance.getAllEntityData()) {
				if (data.getType().name().equalsIgnoreCase(args[0])) {
					String strData = data.getData();
					if (!args[1].equals("")) {
						if (strData != null) {
							if (!strData.startsWith(args[1])) { 
								continue; 
							}
						}
					}
					if (strData == null) {
						strData = "NONE";
					}
					
					if (!completes.contains(strData)) {
						completes.add(strData);
					}
				}
			}
		}
		return completes;
	}
}
