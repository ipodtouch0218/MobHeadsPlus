package me.ipodtouch0218.mobheadsplus;

import java.util.ArrayList;

import org.bukkit.Material;
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
		if (!(e.getEntity() instanceof LivingEntity)) { 
			return; 
		}
		
		LivingEntity en = (LivingEntity) e.getEntity();
		if (en.getKiller() == null) { 
			return; 
		} //CHECK IF KILLER PLAYER EXISTS
		Player killer = en.getKiller();
		
		//if (!killer.hasPermission("")) { return; } //PLAYER DOESNT HAVE PERMISSION
		
		EntityData data = instance.getEntityData(en, Utils.getDataFromEntity(en));
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
		
		ItemStack item = ((Item) e.getCaught()).getItemStack();
		EntityData data = instance.getEntityData(e.getCaught(), "" + item.getDurability());
	
		double chance = data.getBaseDropChance();
		double generated = Math.random();
		if (generated <= chance) {
			ItemStack skull = data.getSkull();
			((Item) e.getCaught()).setItemStack(skull);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() != Material.SKULL) { 
			return; 
		}
		
		ArrayList<ItemStack> newDrops = new ArrayList<>();
		e.setDropItems(false);
		for (ItemStack item : e.getBlock().getDrops()) {
			if (item.getType() != Material.SKULL_ITEM) { 
				continue; 
			}
			
			SkullMeta droppedMeta = (SkullMeta) item.getItemMeta();
		    Class<?> headMetaClass = droppedMeta.getClass();
		    GameProfile profile = Reflections.getField(headMetaClass, "profile", GameProfile.class).get(droppedMeta);
			
			dataLoop:
			for (EntityData data : instance.getAllEntityData()) {
				if (profile.getName().equals(data.getOwnerName())) {
					e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), data.getSkull());
					break dataLoop;
				}
			}
		}
		
		newDrops.forEach(it -> e.getBlock().getLocation().add(.5,0,.5).getWorld().dropItemNaturally(e.getBlock().getLocation().add(.5,0,.5), it));
	}
}
