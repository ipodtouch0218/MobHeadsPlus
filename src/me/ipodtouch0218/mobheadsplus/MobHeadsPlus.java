package me.ipodtouch0218.mobheadsplus;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
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
	}
	
	@Override
	public void onDisable() {
		Skull.instance = null;
	}

	
	public void loadDropChances() {
		headDropChances.clear();
		
		ConfigurationSection mobConfig = getConfig().getConfigurationSection("mobs");
		for (String entity : mobConfig.getValues(false).keySet()) {
			ConfigurationSection entitySection = mobConfig.getConfigurationSection(entity);
			
			EntityType eType = EntityType.valueOf(entity);
			for (String subEn : entitySection.getValues(false).keySet()) {
				ConfigurationSection subSection = entitySection.getConfigurationSection(subEn);
				EntityData data = new EntityData(eType, subSection);
				
				if (!headDropChances.contains(data)) headDropChances.add(data);
			}
		}
		
		ConfigurationSection fishConfig = getConfig().getConfigurationSection("fish");
		EntityType eType = EntityType.DROPPED_ITEM;
		for (String fishType : fishConfig.getValues(false).keySet()) {
			if (!fishConfig.isConfigurationSection(fishType)) { continue; }
			ConfigurationSection fishSection = fishConfig.getConfigurationSection(fishType);
			
			EntityData data = new EntityData(eType, fishSection);
			if (!headDropChances.contains(data)) headDropChances.add(data);
		}
	}
	
	public EntityData getEntityData(Entity en, String data) {
		for (EntityData enData : headDropChances) {
			if (enData.equalsEntity(en, data)) { return enData; }
		}
		return null;
	}
	
	public ArrayList<EntityData> getAllEntityData() { return headDropChances; }
}
