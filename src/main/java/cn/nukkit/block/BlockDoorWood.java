package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoorWood extends BlockDoor {

    public BlockDoorWood() {
        this(0);
    }

    public BlockDoorWood(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Wood Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_WOODEN_DOOR;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.WOODEN_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
