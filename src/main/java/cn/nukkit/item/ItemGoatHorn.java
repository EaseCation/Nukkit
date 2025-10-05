package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemGoatHorn extends Item {
    public static final int PONDER_GOAT_HORN = 0;
    public static final int SING_GOAT_HORN = 1;
    public static final int SEEK_GOAT_HORN = 2;
    public static final int FEEL_GOAT_HORN = 3;
    public static final int ADMIRE_GOAT_HORN = 4;
    public static final int CALL_GOAT_HORN = 5;
    public static final int YEARN_GOAT_HORN = 6;
    public static final int DREAM_GOAT_HORN = 7;
    public static final int UNDEFINED_GOAT_HORN = 8;

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
    public boolean isStackedByData() {
        return true;
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
            case PONDER_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_0);
                break;
            case SING_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_1);
                break;
            case SEEK_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_2);
                break;
            case FEEL_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_3);
                break;
            case ADMIRE_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_4);
                break;
            case CALL_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_5);
                break;
            case YEARN_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_6);
                break;
            case DREAM_GOAT_HORN:
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_GOAT_CALL_7);
                break;
        }

        player.startItemCooldown("goat_horn", COOLDOWN);
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
    }

    @Override
    public int getUseDuration() {
        return 20;
    }
}
