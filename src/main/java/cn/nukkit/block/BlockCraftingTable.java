package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.types.ContainerType;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/5 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockCraftingTable extends BlockSolid {
    public BlockCraftingTable() {
    }

    @Override
    public String getName() {
        return "Crafting Table";
    }

    @Override
    public int getId() {
        return CRAFTING_TABLE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            player.craftingType = Player.CRAFTING_BIG;
            player.recipeTag = RecipeTag.CRAFTING_TABLE;
            player.setCraftingGrid(player.getUIInventory().getBigCraftingGrid());

            ContainerOpenPacket pk = new ContainerOpenPacket();
//            pk.windowId = Player.WORKBENCH_WINDOW_ID;
            pk.windowId = -1;
            pk.type = ContainerType.WORKBENCH;
            pk.x = (int) x;
            pk.y = (int) y;
            pk.z = (int) z;
            player.dataPacket(pk);
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
