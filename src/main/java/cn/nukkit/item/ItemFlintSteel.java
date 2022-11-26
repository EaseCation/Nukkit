package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFlintSteel extends ItemTool {

    public ItemFlintSteel() {
        this(0, 1);
    }

    public ItemFlintSteel(Integer meta) {
        this(meta, 1);
    }

    public ItemFlintSteel(Integer meta, int count) {
        super(FLINT_AND_STEEL, meta, count, "Flint and Steel");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        if (block.getId() == AIR) {
            if (!player.isCreative() && useOn(block)) {
                if (getDamage() >= getMaxDurability()) {
                    pop();
                }
                player.getInventory().setItemInHand(this);
            }

            if (!BlockFire.tryIgnite(block, null, player, BlockIgniteCause.FLINT_AND_STEEL)) {
                return false;
            }

            level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_IGNITE);
            return true;
        }
        return false;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FLINT_STEEL;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }
}
