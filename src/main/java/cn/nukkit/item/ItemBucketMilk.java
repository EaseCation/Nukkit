package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemBucketMilk extends ItemEdible {
    public ItemBucketMilk() {
        this(0, 1);
    }

    public ItemBucketMilk(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketMilk(Integer meta, int count) {
        super(MILK_BUCKET, meta, count, "Milk Bucket");
    }

    @Override
    public String getDescriptionId() {
        return "item.milk.name";
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
        if (ticksUsed < getUseDuration() - 2) {
            return false;
        }

        PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(player, this);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        player.removeAllEffects();

        if (!player.isCreative()) {
            pop();
            player.getInventory().setItemInHand(get(BUCKET));
        }
        return true;
    }

    @Override
    protected int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_DRINK;
    }

    @Override
    public boolean isBucket() {
        return true;
    }
}
