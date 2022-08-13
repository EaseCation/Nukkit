package cn.nukkit.potion;

import cn.nukkit.GameVersion;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.potion.Potion.*;

public class Potions {

    public static void registerVanillaPotions() {
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
