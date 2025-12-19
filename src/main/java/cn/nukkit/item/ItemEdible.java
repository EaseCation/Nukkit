package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemEdible extends Item {
    protected ItemEdible(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    protected ItemEdible(int id) {
        super(id);
    }

    protected ItemEdible(int id, Integer meta) {
        super(id, meta);
    }

    protected ItemEdible(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative() || player.level.getDifficulty() == 0) {
            return true;
        }
        player.getFoodData().sendFoodLevel();
        return false;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (ticksUsed < getUseDuration() - 2) {
            return false;
        }
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        Food food = Food.getByRelative(this);
        if (food != null && food.eatenBy(player)) {
            if (player.isSurvivalLike()) {
                --this.count;
                player.getInventory().setItemInHand(this);
            }

            int sound = getSoundEvent();
            if (sound != -1) {
                player.level.addLevelSoundEvent(player.getEyePosition(), sound);
            }
        }
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
        return 32;
    }

    protected int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_BURP;
    }
}
