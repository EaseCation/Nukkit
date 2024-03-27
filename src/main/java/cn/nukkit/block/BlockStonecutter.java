package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.StonecutterInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockStonecutter extends BlockTransparentMeta implements Faceable {

    public BlockStonecutter() {
        this(0);
    }

    public BlockStonecutter(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONECUTTER_BLOCK;
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public float getResistance() {
        return 17.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    this.toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public double getMaxY() {
        return this.y + 9 / 16.0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setDamage(player.getHorizontalFacing().getOpposite().getIndex());
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (player == null) {
            return false;
        }
        player.addWindow(new StonecutterInventory(player.getUIInventory(), this), Player.WORKBENCH_WINDOW_ID);
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & 0b111);
    }
}
