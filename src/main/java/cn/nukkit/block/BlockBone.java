package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * @author CreeperFace
 */
public class BlockBone extends BlockSolid implements Faceable {

    private static final int[] FACES = {
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
    };

    public BlockBone() {
        this(0);
    }

    public BlockBone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BONE_BLOCK;
    }

    @Override
    public String getName() {
        return "Bone Block";
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem(true)};
        }

        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(((this.getDamage() & 0x3) | FACES[face.getIndex()]));
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
