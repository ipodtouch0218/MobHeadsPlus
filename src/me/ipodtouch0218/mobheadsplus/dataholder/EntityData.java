package me.ipodtouch0218.mobheadsplus.dataholder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.ipodtouch0218.mobheadsplus.externallibs.Skull;

public class EntityData {
	
	private EntityType type;
	private String data;
	private String customName;
	private String ownerName;
	private String texture;
	
	private float baseChance = 0.01f;
	private float lootingIncrease = 0.01f;
	
	private ItemStack skull;
	
	public EntityData(EntityType type, ConfigurationSection sec) {
		this.type = type;
		this.data = sec.getName().equals("DEFAULT") ? null : sec.getName();
	
		this.baseChance = (float) sec.getDouble("base");
		if (sec.isSet("looting")) {
			this.lootingIncrease = (float) sec.getDouble("looting");
		}
		
		String texture = sec.getString("texture");
		if (texture.matches("\\d+")) {
			this.skull = new ItemStack(Material.SKULL_ITEM, 1, Short.parseShort(texture));
		} else {
			ownerName = (type + "-" + sec.getName());
			this.skull = Skull.getCustomSkull(sec.getString("texture"), ownerName);
			texture = sec.getString("texture");
		}
		
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		if (sec.isSet("name") && !sec.getString("name").equals("")) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', sec.getString("name")));
			customName = meta.getDisplayName();
		}
		skull.setItemMeta(meta);
	}
	
	public String getTexture() { return texture; }
	public String getCustomName() { return customName; }
	public String getOwnerName() { return ownerName; }
	public String getData() { return data; }
	public EntityType getType() { return type; }
	public float getBaseDropChance() { return baseChance; }
	public float getLootingIncreaseChance() { return lootingIncrease; }
	public ItemStack getSkull() { return skull; }
	
	public boolean equalsEntity(Entity en, String data) {
		if (en.getType() != type) { return false; }
		if (data == null && this.data == null) { return true; }
		if (!data.equals(this.data)) { return false; }
		
		return true;
	}
}
