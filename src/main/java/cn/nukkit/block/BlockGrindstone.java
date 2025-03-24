package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockGrindstone extends BlockTransparent implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int DIRECTION_BITS = 2;
    public static final int ATTACHMENT_MASK = 0b1100;

    public static final int ATTACHMENT_STANDING = 0b00;
    public static final int ATTACHMENT_HANGING = 0b01;
    public static final int ATTACHMENT_SIDE = 0b10;
    public static final int ATTACHMENT_MULTIPLE = 0b11;

    public BlockGrindstone() {
        this(0);
    }

    public BlockGrindstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Grindstone";
    }

    @Override
    public int getId() {
        return GRINDSTONE;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        }
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (getSide(face.getOpposite()).isAir()) {
            return false;
        }

        switch (face) {
            case UP:
                setDamage((ATTACHMENT_STANDING << DIRECTION_BITS) | (player != null ?
                        player.getHorizontalFacing().getOpposite().getHorizontalIndex() : getDamage() & DIRECTION_MASK));
                break;
            case DOWN:
                setDamage((ATTACHMENT_HANGING << DIRECTION_BITS) | (player != null ?
                        player.getHorizontalFacing().getOpposite().getHorizontalIndex() : getDamage() & DIRECTION_MASK));
                break;
            default:
                setDamage((ATTACHMENT_SIDE << DIRECTION_BITS) | face.getHorizontalIndex());
                break;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }
        player.addWindow(new GrindstoneInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID);
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (getSide(getBlockFace().getOpposite()).isAir()) {
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getBlockFace().getAxis()) {
            default:
            case Y:
                return shrink(2 / 16.0, 0, 2 / 16.0);
            case X:
                return shrink(0, 2 / 16.0, 2 / 16.0);
            case Z:
                return shrink(2 / 16.0, 2 / 16.0, 0);
        }
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        switch (getAttachment()) {
            case ATTACHMENT_STANDING:
                return BlockFace.UP;
            case ATTACHMENT_HANGING:
                return BlockFace.DOWN;
            default:
                return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
        }
    }

    public int getAttachment() {
        return (getDamage() & ATTACHMENT_MASK) >> DIRECTION_BITS;
    }
}
