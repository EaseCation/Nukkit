package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockLoom extends BlockSolidMeta implements Faceable {

    public static final int DIRECTION_MASK = 0b11;

    public BlockLoom() {
        this(0);
    }

    public BlockLoom(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Loom";
    }

    @Override
    public int getId() {
        return LOOM;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
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
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex());
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
        player.addWindow(new LoomInventory(player.getUIInventory(), this), Player.WORKBENCH_WINDOW_ID);
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }
}
