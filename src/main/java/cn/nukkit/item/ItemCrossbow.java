package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

//TODO
public class ItemCrossbow extends ItemTool implements ItemReleasable {

    public ItemCrossbow() {
        this(0, 1);
    }

    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
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
    public boolean onClickAir(Player player, Vector3 directionVector) {
        //TODO
        return true;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        //TODO
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        //TODO
        return true;
    }
}
