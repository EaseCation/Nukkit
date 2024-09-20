package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.item in project Nukkit.
 */
public class ItemSkull extends Item {
    public static final int HEAD_SKELETON = 0;
    public static final int HEAD_WITHER_SKELETON = 1;
    public static final int HEAD_ZOMBIE = 2;
    public static final int HEAD_PLAYER = 3;
    public static final int HEAD_CREEPER = 4;
    public static final int HEAD_DRAGON = 5;
    public static final int HEAD_PIGLIN = 6;
    public static final int HEAD_UNDEFINED = 7;

    public ItemSkull() {
        this(0, 1);
    }

    public ItemSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemSkull(Integer meta, int count) {
        super(SKULL, meta, count, getItemSkullName(meta != null ? meta : 0));
        this.block = Block.get(Block.BLOCK_SKULL);
    }

    @Override
    public int getEquippingSound() {
        return LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
    }

    public static String getItemSkullName(int meta) {
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
