package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.Map;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockChest extends BlockTransparentMeta implements Faceable {

    protected static final int[] FACES = {2, 5, 3, 4};

    public BlockChest() {
        this(0);
    }

    public BlockChest(int meta) {
        super(meta);
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
    public String getName() {
        return "Chest";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CHEST;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinY() {
        return this.y;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityChest chest = null;
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        for (int side = 2; side <= 5; ++side) {
            if ((this.getDamage() == 4 || this.getDamage() == 5) && (side == 4 || side == 5)) {
                continue;
            } else if ((this.getDamage() == 3 || this.getDamage() == 2) && (side == 2 || side == 3)) {
                continue;
            }
            Block c = this.getSide(BlockFace.fromIndex(side));
            if (c.getId() == this.getId() && c.getDamage() == this.getDamage()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c);
                if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    chest = (BlockEntityChest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHEST)
                .putList(new ListTag<>("Items"));

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
    public boolean onBreak(Item item) {
        BlockEntity t = this.getLevel().getBlockEntity(this);
        if (t instanceof BlockEntityChest) {
            ((BlockEntityChest) t).unpair();
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (player != null) {
            Block top = up();
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityChest chest;
            if (t instanceof BlockEntityChest) {
                chest = (BlockEntityChest) t;
            } else {
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHEST)
                        .putList(new ListTag<>("Items"));
                chest = (BlockEntityChest) BlockEntities.createBlockEntity(BlockEntityType.CHEST, getChunk(), nbt);
                if (chest == null) {
                    return false;
                }
            }

            if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag) {
                if (!chest.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(chest.getInventory());
        }

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
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
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
        return false;
    }
}
