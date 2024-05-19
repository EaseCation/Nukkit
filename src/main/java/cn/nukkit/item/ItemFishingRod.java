package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemFishingRod extends ItemTool {

    public ItemFishingRod() {
        this(0, 1);
    }

    public ItemFishingRod(Integer meta) {
        this(meta, 1);
    }

    public ItemFishingRod(Integer meta, int count) {
        super(FISHING_ROD, meta, count, "Fishing Rod");
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.fishing != null) {
            int itemDamage = player.stopFishing(true);
            if (itemDamage > 0 && player.isSurvivalLike() && hurtAndBreak(itemDamage) < 0) {
                pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
        } else {
            player.startFishing(this);
        }
        return true;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FISHING_ROD;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
