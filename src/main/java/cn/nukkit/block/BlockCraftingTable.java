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
    BlockCraftingTable() {

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
            if (!player.deferWindowOpen(() -> this.openCraftingTable(player))) {
                this.openCraftingTable(player);
            }
        }
        return true;
    }

    private void openCraftingTable(Player player) {
        player.craftingType = Player.CRAFTING_BIG;
        player.recipeTag = RecipeTag.CRAFTING_TABLE;
        player.setCraftingGrid(player.getUIInventory().getBigCraftingGrid());

        ContainerOpenPacket packet = new ContainerOpenPacket();
//        packet.windowId = Player.WORKBENCH_WINDOW_ID;
        packet.windowId = -1;
        player.setLastOpenedWindowId(-1);
        packet.type = ContainerType.WORKBENCH;
        packet.x = (int) x;
        packet.y = (int) y;
        packet.z = (int) z;
        player.dataPacket(packet);
    }

    @Override
    public boolean hasUI() {
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

    @Override
    public Instrument getInstrument() {
        return Instrument.BASS;
    }
}
