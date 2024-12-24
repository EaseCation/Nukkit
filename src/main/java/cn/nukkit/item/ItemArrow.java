package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemArrow extends Item {
    public static final int NORMAL_ARROW = 0;
    public static final int TIPPED_ARROW = 1;

    public ItemArrow() {
        this(0, 1);
    }

    public ItemArrow(Integer meta) {
        this(meta, 1);
    }

    public ItemArrow(Integer meta, int count) {
        super(ARROW, meta, count, "Arrow");
    }

    @Override
    public boolean canDualWield() {
        return true;
    }

    @Override
    public int getEquippingSound() {
        return LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
    }

    public boolean isTipped() {
        return getDamage() > NORMAL_ARROW;
    }

    public int getPotionId() {
        return getDamage() - TIPPED_ARROW;
    }
}
