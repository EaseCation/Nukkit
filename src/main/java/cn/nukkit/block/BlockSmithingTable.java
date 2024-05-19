package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.SmithingTableInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockSmithingTable extends BlockSolid {

    public BlockSmithingTable() {
    }

    @Override
    public String getName() {
        return "Smithing Table";
    }

    @Override
    public int getId() {
        return SMITHING_TABLE;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public float getHardness() {
        return 2.5f;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (player == null) {
            return false;
        }
        player.addWindow(new SmithingTableInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID);
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
