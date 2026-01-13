package cn.nukkit.item;

import cn.nukkit.block.*;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import static cn.nukkit.GameVersion.*;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.item in project Nukkit.
 * @deprecated flattened
 * @see BlockSkull
 * @see BlockSkullSkeleton
 * @see BlockSkullWitherSkeleton
 * @see BlockSkullZombie
 * @see BlockSkullPlayer
 * @see BlockSkullCreeper
 * @see BlockSkullDragon
 * @see BlockSkullPiglin
 */
@Deprecated
public class ItemSkull extends Item {
    public static final int HEAD_SKELETON = 0;
    public static final int HEAD_WITHER_SKELETON = 1;
    public static final int HEAD_ZOMBIE = 2;
    public static final int HEAD_PLAYER = 3;
    public static final int HEAD_CREEPER = 4;
    public static final int HEAD_DRAGON = 5;
    public static final int HEAD_PIGLIN = 6;
    public static final int HEAD_UNDEFINED = 7;

    private static final String[] SKULL_TYPE_NAMES = {
            "skeleton",
            "wither",
            "zombie",
            "char",
            "creeper",
            "dragon",
            "piglin",
    };

    public ItemSkull() {
        this(0, 1);
    }

    public ItemSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemSkull(Integer meta, int count) {
        super(SKULL, meta, count, "Skull");
//        this.block = Block.get(Block.BLOCK_SKULL);
    }

    @Override
    public String getDescriptionId() {
        int type = getDamage();
        if (type >= 0 && type < SKULL_TYPE_NAMES.length) {
            return "item.skull." + SKULL_TYPE_NAMES[type] + ".name";
        }
        return "item.skull.skeleton.name";
    }

    @Override
    public boolean isStackedByData() {
        return true || !V1_21_40.isAvailable();
    }

    @Override
    public int getEquippingSound() {
        return LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
    }

    private static String getItemSkullName(int meta) {
        switch (meta) {
            case 1:
                return "Wither Skeleton Skull";
            case 2:
                return "Zombie Head";
            case 3:
                return "Player Head";
            case 4:
                return "Creeper Head";
            case 5:
                return "Dragon Head";
            case 6:
                return "Piglin Head";
            case 0:
            default:
                return "Skeleton Skull";
        }
    }
}
