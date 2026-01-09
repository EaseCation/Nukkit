package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockChest extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;

    BlockChest() {

    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return CHEST;
    }

    @Override
    public int getBlockDefaultMeta() {
        return 2;
    }

    @Override
    public int getItemSerializationMeta() {
        return getBlockDefaultMeta();
    }

    @Override
    public String getName() {
        return "Chest";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CHEST;
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
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9475;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntityChest chest = null;
        Block pairBlock = null;
        int pairableId = -1;

        BlockFace facing = player != null ? player.getDirection().getOpposite() : BlockFace.NORTH;
        this.setDamage(facing.getHorizontalIndex());

        for (BlockFace side : BlockFace.Plane.HORIZONTAL) {
            if (side.getAxis() == facing.getAxis()) {
                continue;
            }

            Block c = this.getSide(side);
            int pairableBlockId = getPairableBlockId(c);
            if (pairableBlockId != -1 && c.getDamage() == this.getDamage()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c);
                if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    chest = (BlockEntityChest) blockEntity;
                    pairBlock = c;
                    pairableId = pairableBlockId;
                    break;
                }
            }
        }

        Block placeBlock = this;
        if (pairBlock != null) {
            if (pairBlock.getId() != pairableId) {
                level.setBlock(pairBlock, get(pairableId, pairBlock.getDamage()), true, false);
            }
            if (getId() != pairableId) {
                placeBlock = get(pairableId, getDamage());
            }
        }
        this.getLevel().setBlock(block, placeBlock, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHEST);

        CompoundTag itemNbt = item.getNamedTag();
        nbt.putList(itemNbt != null ? itemNbt.getList("Items", CompoundTag.class) : new ListTag<>("Items"));

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityChest blockEntity = (BlockEntityChest) BlockEntities.createBlockEntity(BlockEntityType.CHEST, getChunk(), nbt);

        if (blockEntity == null) {
            return false;
        }

        if (chest != null) {
            chest.pairWith(blockEntity);
            blockEntity.pairWith(chest);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        BlockEntity t = this.getLevel().getBlockEntity(this);
        if (t instanceof BlockEntityChest) {
            ((BlockEntityChest) t).unpair();
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            Block top = up();
            if (!top.isTransparent() || top instanceof BlockSlab slab && !slab.isTopSlot()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityChest chest;
            if (t instanceof BlockEntityChest) {
                chest = (BlockEntityChest) t;
            } else {
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHEST);

                CompoundTag itemNbt = item.getNamedTag();
                nbt.putList(itemNbt != null ? itemNbt.getList("Items", CompoundTag.class) : new ListTag<>("Items"));

                chest = (BlockEntityChest) BlockEntities.createBlockEntity(BlockEntityType.CHEST, getChunk(), nbt);
                if (chest == null) {
                    return true;
                }
            }

            if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag) {
                if (!chest.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            chest.unpackLootTable();
            player.addWindow(chest.getInventory());
        }

        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityChest) {
            return ContainerInventory.calculateRedstone(((BlockEntityChest) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(this.getItemId());
        if (addUserData) {
            BlockEntity blockEntity = getBlockEntity();
            if (blockEntity != null) {
                item.setCustomName(blockEntity.getName());
                item.setRepairCost(blockEntity.getRepairCost());
            }
        }
        return item;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & DIRECTION_MASK);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN && type == SupportType.CENTER;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isChest() {
        return true;
    }

    @Nullable
    protected BlockEntityChest getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityChest blockEntity) {
            return blockEntity;
        }
        return null;
    }

    protected int getPairableBlockId(Block block) {
        int id = block.getId();
        return getId() == id ? id : -1;
    }
}
