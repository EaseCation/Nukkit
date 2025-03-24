package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.CartographyTableInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockCartographyTable extends BlockSolid {

    public BlockCartographyTable() {
    }

    @Override
    public String getName() {
        return "Cartography Table";
    }

    @Override
    public int getId() {
        return CARTOGRAPHY_TABLE;
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
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }
        player.addWindow(new CartographyTableInventory(player.getUIInventory(), this), Player.WORKBENCH_WINDOW_ID);
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
