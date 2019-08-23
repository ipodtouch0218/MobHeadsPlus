package me.ipodtouch0218.mobheadsplus.utils;

import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.ZombieVillager;

public class Utils {

	public static String getDataFromEntity(Entity en) {
		Object toReturn = null;
		switch(en.getType()) {
			case CAT: toReturn = ((Cat) en).getCatType();
			case SHEEP: toReturn = en.getCustomName() != null && en.getCustomName().equals("jeb_") ? "JEB" : ((Sheep) en).getColor(); break;
			case HORSE: toReturn = ((Horse) en).getColor(); break;
			case CREEPER: toReturn = ((Creeper) en).isPowered() ? "CHARGED" : null; break;
			case FOX: toReturn = ((Fox) en).getFoxType(); break;
			case OCELOT: toReturn = ((Ocelot) en).getCatType(); break;
			case PANDA: toReturn = ((Panda) en).getMainGene(); break;
			case PARROT: toReturn = ((Parrot) en).getVariant(); break;
			case RABBIT: toReturn = ((Rabbit) en).getRabbitType(); break;
			case LLAMA: toReturn = ((Llama) en).getColor(); break;
			case TRADER_LLAMA: toReturn = ((TraderLlama) en).getColor(); break;
			case ZOMBIE_VILLAGER: toReturn = ((ZombieVillager) en).getVillagerProfession(); break;
			default: break;
		}
		
		return toReturn != null ? toReturn.toString() : null;
	}
	
}
