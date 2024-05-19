package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockHayBale extends BlockSolidMeta implements Faceable {

    private static final int[] FACES = new int[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100,
    };

    public BlockHayBale() {
        this(0);
    }

    public BlockHayBale(int meta) {
        super(meta);
     }

    @Override
    public int getId() {
        return HAY_BLOCK;
    }

    @Override
    public String getName() {
        return "Hay Bale";
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage((this.getDamage() & 0x03) | FACES[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getId(), 0);
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }
}
