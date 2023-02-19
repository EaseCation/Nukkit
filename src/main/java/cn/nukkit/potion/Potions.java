package cn.nukkit.potion;

import cn.nukkit.GameVersion;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.potion.PotionID.*;

public final class Potions {

    public static void registerVanillaPotions() {
        registerPotion(WATER, new Potion(Potion.WATER));
        registerPotion(MUNDANE, new Potion(Potion.MUNDANE));
        registerPotion(MUNDANE_II, new Potion(Potion.MUNDANE_II, 2));
        registerPotion(THICK, new Potion(Potion.THICK));
        registerPotion(AWKWARD, new Potion(Potion.AWKWARD));
        registerPotion(NIGHT_VISION, new Potion(Potion.NIGHT_VISION));
        registerPotion(NIGHT_VISION_LONG, new Potion(Potion.NIGHT_VISION_LONG));
        registerPotion(INVISIBLE, new Potion(Potion.INVISIBLE));
        registerPotion(INVISIBLE_LONG, new Potion(Potion.INVISIBLE_LONG));
        registerPotion(LEAPING, new Potion(Potion.LEAPING));
        registerPotion(LEAPING_LONG, new Potion(Potion.LEAPING_LONG));
        registerPotion(LEAPING_II, new Potion(Potion.LEAPING_II, 2));
        registerPotion(FIRE_RESISTANCE, new Potion(Potion.FIRE_RESISTANCE));
        registerPotion(FIRE_RESISTANCE_LONG, new Potion(Potion.FIRE_RESISTANCE_LONG));
        registerPotion(SPEED, new Potion(Potion.SPEED));
        registerPotion(SPEED_LONG, new Potion(Potion.SPEED_LONG));
        registerPotion(SPEED_II, new Potion(Potion.SPEED_II, 2));
        registerPotion(SLOWNESS, new Potion(Potion.SLOWNESS));
        registerPotion(SLOWNESS_LONG, new Potion(Potion.SLOWNESS_LONG));
        registerPotion(WATER_BREATHING, new Potion(Potion.WATER_BREATHING));
        registerPotion(WATER_BREATHING_LONG, new Potion(Potion.WATER_BREATHING_LONG));
        registerPotion(INSTANT_HEALTH, new Potion(Potion.INSTANT_HEALTH));
        registerPotion(INSTANT_HEALTH_II, new Potion(Potion.INSTANT_HEALTH_II, 2));
        registerPotion(HARMING, new Potion(Potion.HARMING));
        registerPotion(HARMING_II, new Potion(Potion.HARMING_II, 2));
        registerPotion(POISON, new Potion(Potion.POISON));
        registerPotion(POISON_LONG, new Potion(Potion.POISON_LONG));
        registerPotion(POISON_II, new Potion(Potion.POISON_II, 2));
        registerPotion(REGENERATION, new Potion(Potion.REGENERATION));
        registerPotion(REGENERATION_LONG, new Potion(Potion.REGENERATION_LONG));
        registerPotion(REGENERATION_II, new Potion(Potion.REGENERATION_II, 2));
        registerPotion(STRENGTH, new Potion(Potion.STRENGTH));
        registerPotion(STRENGTH_LONG, new Potion(Potion.STRENGTH_LONG));
        registerPotion(STRENGTH_II, new Potion(Potion.STRENGTH_II, 2));
        registerPotion(WEAKNESS, new Potion(Potion.WEAKNESS));
        registerPotion(WEAKNESS_LONG, new Potion(Potion.WEAKNESS_LONG));
        registerPotion(WITHER_II, new Potion(WITHER_II, 2));

        registerPotion(TURTLE_MASTER, new Potion(TURTLE_MASTER), V1_5_0);
        registerPotion(TURTLE_MASTER_LONG, new Potion(TURTLE_MASTER_LONG), V1_5_0);
        registerPotion(TURTLE_MASTER_II, new Potion(TURTLE_MASTER_II, 2), V1_5_0);

        registerPotion(SLOW_FALLING, new Potion(SLOW_FALLING), V1_6_0);
        registerPotion(SLOW_FALLING_LONG, new Potion(SLOW_FALLING_LONG), V1_6_0);

        registerPotion(SLOWNESS_IV, new Potion(SLOWNESS_IV, 4), V1_16_0);

    }

    private static Potion registerPotion(int id, Potion potion) {
        if (Potion.potions[id] == null) {
            Potion.potions[id] = potion;
        }
        return Potion.potions[id];
    }

    /**
     * @param version min required base game version
     */
    private static Potion registerPotion(int id, Potion potion, GameVersion version) {
        if (!version.isAvailable()) {
//            return null;
        }
        return registerPotion(id, potion);
    }

    private Potions() {
        throw new IllegalStateException();
    }
}
