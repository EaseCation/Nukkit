package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoorIron extends BlockDoor {

    public BlockDoorIron() {
        this(0);
    }

    public BlockDoorIron(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Iron Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_IRON_DOOR;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getHardness() {
        return 5;
    }

    @Override
    public float getResistance() {
        return 25;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.IRON_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.METAL_BLOCK_COLOR;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }
}
