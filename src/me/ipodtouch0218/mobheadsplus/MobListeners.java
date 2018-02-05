package me.ipodtouch0218.mobheadsplus;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;

import me.ipodtouch0218.mobheadsplus.dataholder.EntityData;
import me.ipodtouch0218.mobheadsplus.externallibs.Reflections;
import me.ipodtouch0218.mobheadsplus.externallibs.Skull;
import me.ipodtouch0218.mobheadsplus.utils.Utils;

public class MobListeners implements Listener {

	private MobHeadsPlus instance;
	
	public MobListeners(MobHeadsPlus inst) {
		this.instance = inst;
	}
	
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onEntityDeath(EntityDeathEvent e) {
		if (!instance.getConfig().getBoolean("mobs.enabled")) {
			return;
		}
		if (!(e.getEntity() instanceof LivingEntity)) { 
			return; 
		}
		
		LivingEntity en = (LivingEntity) e.getEntity();
		if (en.getKiller() == null) { 
			return; 
		} //CHECK IF KILLER PLAYER EXISTS
		Player killer = en.getKiller();
		
		if (instance.getConfig().getBoolean("mobs.require-permission") && !killer.hasPermission(instance.getConfig().getString("mobs.permission"))) { 
			return; 
		} //PLAYER DOESNT HAVE PERMISSION
		
		EntityData data = instance.getEntityData(en.getType(), Utils.getDataFromEntity(en));
		int lootingAmount = 0;
		
		if (killer.getInventory().getItemInMainHand() != null) { //CHECK FOR LOOTING BONUS
			ItemStack killItem = killer.getInventory().getItemInMainHand();
			lootingAmount = killItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
		}
		
		double chance = data.getBaseDropChance() + (lootingAmount*data.getLootingIncreaseChance());
		double generated = Math.random();

		ItemStack skull = data.getSkull();
			
		if (e.getEntityType() == EntityType.PLAYER) {
			Player dead = (Player) e.getEntity();
			String name = skull.getItemMeta().getDisplayName().replace("{0}", dead.getName());
				
			ItemStack playerSkull = Skull.getPlayerSkull(dead.getName());
			ItemMeta meta = playerSkull.getItemMeta();
			meta.setDisplayName(name);
			playerSkull.setItemMeta(meta);
			skull = playerSkull;
		}
			
		if (generated <= chance) {
			e.getDrops().add(skull);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onFishEvent(PlayerFishEvent e) {
		if (!instance.getConfig().getBoolean("fish.enabled")) { 
			return; 
		}
		if (e.getState() != State.CAUGHT_FISH) { 
			return; 
		}
		if (instance.getConfig().getBoolean("fish.require-permission") && !e.getPlayer().hasPermission(instance.getConfig().getString("fish.permission"))) {
			return;
		}
		
		ItemStack item = ((Item) e.getCaught()).getItemStack();
		EntityData data = instance.getEntityData(e.getCaught().getType(), "" + item.getDurability());
	
		double chance = data.getBaseDropChance();
		double generated = Math.random();
		if (generated <= chance) {
			ItemStack skull = data.getSkull();
			((Item) e.getCaught()).setItemStack(skull);
		}
	}
		
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() != Material.SKULL) { 
			return; 
		}
		
		ArrayList<ItemStack> newDrops = new ArrayList<>();
		e.setDropItems(false);
		bigLoop:
		for (ItemStack item : e.getBlock().getDrops()) {
			if (item.getType() != Material.SKULL_ITEM) { 
				continue; 
			}
			SkullType type = ((org.bukkit.block.Skull) e.getBlock().getState()).getSkullType();
			if (type != SkullType.PLAYER) {
				String compString = type.name();
				if (type == SkullType.DRAGON) {
					compString = "ENDER_DRAGON"; //because "Dragon" isnt the entity type name...
				}
				for (EntityData data : instance.getAllEntityData()) {
					if (data.getType().name().equals(compString)) {
						newDrops.add(data.getSkull());
						continue bigLoop;
					}
				}
				continue;
			}
			
			SkullMeta droppedMeta = (SkullMeta) item.getItemMeta();
			Class<?> headMetaClass = droppedMeta.getClass();
			GameProfile profile = Reflections.getField(headMetaClass, "profile", GameProfile.class).get(droppedMeta);
			
			for (EntityData data : instance.getAllEntityData()) {
				if (profile.getName().equals(data.getOwnerName())) {
					newDrops.add(data.getSkull());
					continue bigLoop;
				}
			}
			
			//No matches for default skulls, now parsing player data.
			
			EntityData playerData = instance.getEntityData(EntityType.PLAYER, null);
			droppedMeta.setDisplayName(playerData.getSkull().getItemMeta().getDisplayName().replace("{0}", droppedMeta.getOwner()));
			item.setDurability((short) 3);
			item.setItemMeta(droppedMeta);
			
			newDrops.add(item);
		}
		
		newDrops.forEach(it -> e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation().add(.5,0,.5), it));
	}
}