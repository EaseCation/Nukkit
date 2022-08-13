package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockBeehive extends BlockSolidMeta implements Faceable {

    public static final int MAX_HONEY_LEVEL = 5;

    public static final int DIRECTION_MASK = 0b11;
    public static final int DIRECTION_BITS = 2;

    public BlockBeehive() {
        this(0);
    }

    public BlockBeehive(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEEHIVE;
    }

    @Override
    public String getName() {
        return "Beehive";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BEEHIVE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 27;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getHoneyLevel() << DIRECTION_BITS);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == ItemID.GLASS_BOTTLE) {
            //TODO

            return true;
        }
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            this.setDamage((getHoneyLevel() << DIRECTION_BITS) | player.getHorizontalFacing().getOpposite().getHorizontalIndex());
        }
        level.setBlock(this, this, true);
        createBlockEntity(item);
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    protected BlockEntityBeehive createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BEEHIVE);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityBeehive) BlockEntity.createBlockEntity(BlockEntity.BEEHIVE, getChunk(), nbt);
    }

    protected BlockEntityBeehive getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityBeehive) {
            return (BlockEntityBeehive) blockEntity;
        }
        return null;
    }

    /**
     * @return 0-5
     */
    public int getHoneyLevel() {
        return getDamage() >> DIRECTION_BITS;
    }

    public void setHoneyLevel(int honeyLevel) {
        setDamage((Mth.clamp(honeyLevel, 0, MAX_HONEY_LEVEL) << DIRECTION_BITS) | (getDamage() & DIRECTION_MASK));
    }
}
