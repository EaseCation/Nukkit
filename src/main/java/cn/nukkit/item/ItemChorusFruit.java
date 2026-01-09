package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

/**
 * Created by Leonidius20 on 20.08.18.
 */
public class ItemChorusFruit extends ItemEdible {

    public ItemChorusFruit() {
        this(0, 1);
    }

    public ItemChorusFruit(Integer meta) {
        this(meta, 1);
    }

    public ItemChorusFruit(Integer meta, int count) {
        super(CHORUS_FRUIT, meta, count, "Chorus Fruit");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return !player.isItemCooling(getCooldownCategory(), getCooldownDuration());
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        boolean successful = super.onUse(player, ticksUsed);
        if (successful) {
            player.startItemCooldown(getCooldownCategory());
        }
        return successful;
    }

    @Override
    public int getCooldownDuration() {
        return 20;
    }

    @Override
    public CooldownCategory getCooldownCategory() {
        return CooldownCategory.CHORUS_FRUIT;
    }
}