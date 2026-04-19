package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockCamera extends BlockSolid {

    BlockCamera() {

    }

    @Override
    public int getId() {
        return CAMERA;
    }

    @Override
    public String getName() {
        return "Camera";
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CAMERA);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }
}
