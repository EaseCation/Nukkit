package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Faceable;

/**
 * @author CreeperFace
 */
public class BlockHopper extends BlockTransparent implements Faceable {

    public BlockHopper() {
        this(0);
    }

    public BlockHopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_HOPPER;
    }

    @Override
    public String getName() {
        return "Hopper Block";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.HOPPER;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 24;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockFace facing = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }

        this.setDamage(facing.getIndex());

        if (this.level.isRedstoneEnabled()) {
            boolean powered = this.level.isBlockPowered(this);

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
            }
        }

        this.level.setBlock(this, this, true);

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.HOPPER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        BlockEntityHopper hopper = (BlockEntityHopper) BlockEntities.createBlockEntity(BlockEntityType.HOPPER, this.level.getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);
        return hopper != null;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return player.addWindow(((BlockEntityHopper) blockEntity).getInventory()) != -1;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return ContainerInventory.calculateRedstone(((BlockEntityHopper) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.getDamage() & 7);
    }

    public boolean isEnabled() {
        return (this.getDamage() & 0x08) != 8;
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            this.setDamage(this.getDamage() ^ 0x08);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean powered = this.level.isBlockPowered(this);

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
                this.level.setBlock(this, this, true, false);

                if (!powered) {
                    BlockEntity be = this.level.getBlockEntity(this);

                    if (be != null) {
                        be.scheduleUpdate();
                    }
                }
            }

            return type;
        }

        return 0;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem(true)};
        }

        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.HOPPER);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.UP || face == BlockFace.DOWN && type == SupportType.CENTER && getBlockFace() == BlockFace.DOWN;
    }
}
