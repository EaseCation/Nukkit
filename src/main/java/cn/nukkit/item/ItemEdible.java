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
    public ItemEdible(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public ItemEdible(int id) {
        super(id);
    }

    public ItemEdible(int id, Integer meta) {
        super(id, meta);
    }

    public ItemEdible(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative() || player.getServer().getDifficulty() == 0) {
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

            player.level.addLevelSoundEvent(player.getEyePosition(), getSoundEvent());
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
