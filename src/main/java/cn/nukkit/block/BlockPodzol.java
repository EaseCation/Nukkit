package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockPodzol extends BlockDirt {

    BlockPodzol() {

    }

    @Override
    public int getId() {
        return PODZOL;
    }

    @Override
    public String getDescriptionId() {
        return "tile." + getShortName() + ".name";
    }

    @Override
    public String getName() {
        return "Podzol";
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
