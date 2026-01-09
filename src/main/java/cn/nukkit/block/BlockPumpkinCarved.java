package cn.nukkit.block;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockPumpkinCarved extends BlockPumpkin {

    BlockPumpkinCarved() {

    }

    @Override
    public int getId() {
        return CARVED_PUMPKIN;
    }

    @Override
    public String getName() {
        return "Carved Pumpkin";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public int getEquippingSound() {
        return LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
    }
}
