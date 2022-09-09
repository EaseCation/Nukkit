package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSand extends BlockFallable {

    public static final int DEFAULT = 0;
    public static final int RED = 1;

    private int meta;

    public BlockSand() {
        this(0);
    }

    public BlockSand(int meta) {
        this.meta = meta;
    }

    @Override
    public int getFullId() {
        return (getId() << BLOCK_META_BITS) | getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }

    @Override
    public int getId() {
        return SAND;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        if (this.getDamage() == 0x01) {
            return "Red Sand";
        }

        return "Sand";
    }

    @Override
    public BlockColor getColor() {
        if (this.getDamage() == 0x01) {
            return BlockColor.ORANGE_BLOCK_COLOR;
        }

        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            //TODO: generate seagrass, coral and coral fan
            return false;
        }
        return false;
    }
}
