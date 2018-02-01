package me.ipodtouch0218.mobheadsplus.utils;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.ZombieVillager;

public class Utils {

	public static String getDataFromEntity(Entity en) {
		Object toReturn = null;
		switch(en.getType()) {
		case SHEEP: toReturn = en.getCustomName() != null && en.getCustomName().equals("jeb_") ? "JEB" : ((Sheep) en).getColor(); break;
		case HORSE: toReturn = ((Horse) en).getColor(); break;
		case CREEPER: toReturn = ((Creeper) en).isPowered() ? "CHARGED" : null; break;
		case OCELOT: toReturn = ((Ocelot) en).getCatType(); break;
		case PARROT: toReturn = ((Parrot) en).getVariant(); break;
		case RABBIT: toReturn = ((Rabbit) en).getRabbitType(); break;
		case LLAMA: toReturn = ((Llama) en).getColor(); break;
		case ZOMBIE_VILLAGER: toReturn = ((ZombieVillager) en).getVillagerProfession(); break;
		default: break;
		}
		
		return toReturn != null ? toReturn.toString() : null;
	}
	
}
