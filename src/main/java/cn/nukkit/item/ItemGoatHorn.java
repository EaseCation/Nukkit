package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemGoatHorn extends Item {

    private static final int COOLDOWN = 7 * 20;

    public ItemGoatHorn() {
        this(0, 1);
    }

    public ItemGoatHorn(Integer meta) {
        this(meta, 1);
    }

    public ItemGoatHorn(Integer meta, int count) {
        super(GOAT_HORN, meta, count, "Goat Horn");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.getServer().getTick() - player.getLastGoatHornPlay() >= COOLDOWN;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        player.onGoatHornPlay();

        switch (getDamage() & 0x7) {
            case 0:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_0);
                break;
            case 1:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_1);
                break;
            case 2:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_2);
                break;
            case 3:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_3);
                break;
            case 4:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_4);
                break;
            case 5:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_5);
                break;
            case 6:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_6);
                break;
            case 7:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_7);
                break;
        }

        player.startItemCooldown("goat_horn", COOLDOWN);
        return true;
    }
}
