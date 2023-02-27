package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockGravel extends BlockFallable {

    public BlockGravel() {
    }

    @Override
    public int getId() {
        return GRAVEL;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Gravel";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (ThreadLocalRandom.current().nextInt(9) == 0) {
            return new Item[]{
                    Item.get(Item.FLINT)
            };
        } else {
            return new Item[]{
                    toItem(true)
            };
        }
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            return BlockSeagrass.trySpawnSeaGrass(this, item, player);
        }
        return false;
    }
}
