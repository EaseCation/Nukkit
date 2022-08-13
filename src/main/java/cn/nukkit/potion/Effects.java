package cn.nukkit.potion;

import cn.nukkit.GameVersion;

import static cn.nukkit.GameVersion.*;

public final class Effects {

    public static void registerVanillaEffects() {
        registerEffect(Effect.CONDUIT_POWER, new Effect(Effect.CONDUIT_POWER, "%potion.conduitPower", 29, 194, 209), V1_5_0);

        registerEffect(Effect.SLOW_FALLING, new Effect(Effect.SLOW_FALLING, "%potion.slowFalling", 206, 255, 255), V1_6_0);

        registerEffect(Effect.BAD_OMEN, new Effect(Effect.BAD_OMEN, "%effect.badOmen", 11, 97, 56, true), V1_11_0);
        registerEffect(Effect.VILLAGE_HERO, new Effect(Effect.VILLAGE_HERO, "%effect.villageHero", 68, 255, 68).setVisible(false), V1_11_0);

        registerEffect(Effect.DARKNESS, new Effect(Effect.DARKNESS, "%effect.darkness", 41, 39, 33, true).setVisible(false), V1_19_0);

    }

    private static Effect registerEffect(int id, Effect effect) {
        if (Effect.effects[id] == null) {
            Effect.effects[id] = effect;
        }
        return Effect.effects[id];
    }

    /**
     * @param version min required base game version
     */
    private static Effect registerEffect(int id, Effect effect, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerEffect(id, effect);
    }

    private Effects() {
        throw new IllegalStateException();
    }
}
