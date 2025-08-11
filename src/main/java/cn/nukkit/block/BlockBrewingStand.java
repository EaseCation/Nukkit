package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockBrewingStand extends BlockTransparent {
    public static final int HAS_SLOT_0 = 0b1;
    public static final int HAS_SLOT_1 = 0b10;
    public static final int HAS_SLOT_2 = 0b100;

    public BlockBrewingStand() {
        this(0);
    }

    public BlockBrewingStand(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Brewing Stand";
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public int getId() {
        return BLOCK_BREWING_STAND;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BREWING_STAND;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BREWING_STAND)
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

        BlockEntityBrewingStand brewing = (BlockEntityBrewingStand) BlockEntities.createBlockEntity(BlockEntityType.BREWING_STAND, getChunk(), nbt);
        return brewing != null;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            BlockEntity t = getLevel().getBlockEntity(this);
            BlockEntityBrewingStand brewing;
            if (t instanceof BlockEntityBrewingStand) {
                brewing = (BlockEntityBrewingStand) t;
            } else {
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BREWING_STAND)
                        .putList(new ListTag<>("Items"));
                brewing = (BlockEntityBrewingStand) BlockEntities.createBlockEntity(BlockEntityType.BREWING_STAND, getChunk(), nbt);
                if (brewing == null) {
                    return true;
                }
            }

            if (brewing.namedTag.contains("Lock") && brewing.namedTag.get("Lock") instanceof StringTag) {
                if (!brewing.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(brewing.getInventory());
        }

        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BREWING_STAND);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.METAL_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMinX() {
        return this.x + 7 / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.z + 7 / 16.0;
    }

    @Override
    public double getMaxX() {
        return this.x + 1 - 7 / 16.0;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 2 / 16.0;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1 - 7 / 16.0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityBrewingStand) {
            return ContainerInventory.calculateRedstone(((BlockEntityBrewingStand) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN && type == SupportType.CENTER;
    }
}
