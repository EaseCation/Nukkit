package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created by PetteriM1
 */
public class ItemFireCharge extends Item {

    public ItemFireCharge() {
        this(0, 1);
    }

    public ItemFireCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemFireCharge(Integer meta, int count) {
        super(FIRE_CHARGE, 0, count, "Fire Charge");
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
            if (!BlockFire.tryIgnite(block, null, player, BlockIgniteCause.FLINT_AND_STEEL)) {
                return false;
            }

            level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_GHAST_SHOOT, 78642);

            if (!player.isCreative()) {
                pop();
                player.getInventory().setItemInHand(this);
            }
            return true;
        }
        return false;
    }
}
