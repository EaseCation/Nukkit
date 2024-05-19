package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

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
    public static final int UNDEFINED_STEW = 10;

    public ItemSuspiciousStew() {
        this(0, 1);
    }

    public ItemSuspiciousStew(Integer meta) {
        this(meta, 1);
    }

    public ItemSuspiciousStew(Integer meta, int count) {
        super(SUSPICIOUS_STEW, meta, 1, "Suspicious Stew");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
