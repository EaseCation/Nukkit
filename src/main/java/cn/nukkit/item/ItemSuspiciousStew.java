package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.EffectID;
import cn.nukkit.utils.Utils;

import java.util.Arrays;

public class ItemSuspiciousStew extends ItemEdible {
    public static final int POPPY_STEW = 0;
    public static final int CORNFLOWER_STEW = 1;
    public static final int TULIP_STEW = 2;
    public static final int AZURE_BLUET_STEW = 3;
    public static final int LILY_OF_THE_VALLEY_STEW = 4;
    public static final int DANDELION_STEW = 5;
    public static final int BLUE_ORCHID_STEW = 6;
    public static final int ALLIUM_STEW = 7;
    public static final int OXEYE_DAISY_STEW = 8;
    public static final int WITHER_ROSE_STEW = 9;
    public static final int TORCHFLOWER_STEW = 10;
    public static final int OPEN_EYEBLOSSOM_STEW = 11;
    public static final int CLOSED_EYEBLOSSOM_STEW = 12;
    public static final int UNDEFINED_STEW = 13;

    private static final int[] BY_EFFECT = Utils.make(new int[EffectID.UNDEFINED], lookup -> {
        Arrays.fill(lookup, -1);
        lookup[EffectID.NIGHT_VISION] = POPPY_STEW;
        lookup[EffectID.JUMP_BOOST] = CORNFLOWER_STEW;
        lookup[EffectID.WEAKNESS] = TULIP_STEW;
        lookup[EffectID.BLINDNESS] = AZURE_BLUET_STEW;
        lookup[EffectID.POISON] = LILY_OF_THE_VALLEY_STEW;
        lookup[EffectID.SATURATION] = DANDELION_STEW;
        lookup[EffectID.FIRE_RESISTANCE] = ALLIUM_STEW;
        lookup[EffectID.REGENERATION] = OXEYE_DAISY_STEW;
        lookup[EffectID.WITHER] = WITHER_ROSE_STEW;
        lookup[EffectID.NAUSEA] = CLOSED_EYEBLOSSOM_STEW;
    });

    public ItemSuspiciousStew() {
        this(0, 1);
    }

    public ItemSuspiciousStew(Integer meta) {
        this(meta, 1);
    }

    public ItemSuspiciousStew(Integer meta, int count) {
        super(SUSPICIOUS_STEW, meta, count, "Suspicious Stew");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    public static int getDataByEffect(int id) {
        if (id < 0 || id >= BY_EFFECT.length) {
            return -1;
        }
        return BY_EFFECT[id];
    }
}
