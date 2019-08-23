package me.ipodtouch0218.mobheadsplus;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import me.ipodtouch0218.mobheadsplus.dataholder.EntityData;
import me.ipodtouch0218.mobheadsplus.externallibs.Skull;

public class MobHeadsPlus extends JavaPlugin {

	private ArrayList<EntityData> headDropChances = new ArrayList<>();
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		Skull.instance = this;
		loadDropChances();
		Bukkit.getPluginManager().registerEvents(new MobListeners(this), this);
		
		CommandSpawnHead spawnHead = new CommandSpawnHead(this);
		getCommand("spawnhead").setExecutor(spawnHead);
		getCommand("spawnhead").setTabCompleter(spawnHead);
	}
	
	@Override
	public void onDisable() {
		Skull.instance = null;
	}

	
	public void loadDropChances() {
		headDropChances.clear();
		
		ConfigurationSection entityConfigurationSection = getConfig().getConfigurationSection("mobs");
		for (String entity : entityConfigurationSection.getValues(false).keySet()) {
			ConfigurationSection specificEntitySection = entityConfigurationSection.getConfigurationSection(entity);
			
			EntityType eType;
			try {
				eType = EntityType.valueOf(entity);
			} catch (IllegalArgumentException e) {
				continue; //Everything is fine... I swear
			}
			
			for (String subEn : specificEntitySection.getValues(false).keySet()) {
				ConfigurationSection subSection = specificEntitySection.getConfigurationSection(subEn);
				EntityData data = new EntityData(eType, subSection);
				
				if (!headDropChances.contains(data)) {
					headDropChances.add(data);
				}
			}
		}
	}
	
	public EntityData getEntityData(EntityType en, String data) {
		for (EntityData enData : headDropChances) {
			if (enData.equalsEntity(en, data)) { 
				return enData; 
			}
		}
		return null;
	}
	
	public ArrayList<EntityData> getAllEntityData() { 
		return headDropChances; 
	}
}
