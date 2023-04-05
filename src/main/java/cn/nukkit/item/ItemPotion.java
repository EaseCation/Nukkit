package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Potion;

public class ItemPotion extends Item {

    public ItemPotion() {
        this(0, 1);
    }

    public ItemPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemPotion(Integer meta, int count) {
        super(POTION, meta, count, "Potion");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (ticksUsed < 30) return false;
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);
        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            return false;
        }

        Potion potion = Potion.getPotion(this.getDamage());
        if (potion != null) {
            potion.applyPotion(player, this);
        }

        if (player.isSurvivalLike()) {
            --this.count;
            player.getInventory().setItemInHand(this);
            player.getInventory().addItem(get(GLASS_BOTTLE));
        }

        return true;
    }

    @Override
    public boolean isPotion() {
        return true;
    }

    public int getPotionId() {
        return getDamage();
    }
}
