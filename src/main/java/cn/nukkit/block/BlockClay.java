package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockClay extends BlockSolid {

    public BlockClay() {
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
    public int getId() {
        return CLAY;
    }

    @Override
    public String getName() {
        return "Clay Block";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.CLAY_BALL, 0, 4)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLAY_BLOCK_COLOR;
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
