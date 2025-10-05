package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnaceBurning extends BlockSolid implements Faceable {

    private static final int[] FACES = {2, 5, 3, 4};

    public BlockFurnaceBurning() {
        this(0);
    }

    public BlockFurnaceBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_FURNACE;
    }

    @Override
    public int getBlockDefaultMeta() {
        return 3;
    }

    @Override
    public String getName() {
        return "Burning Furnace";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.FURNACE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public int getLightLevel() {
        return 13;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, getBlockEntityId())
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

        BlockEntityFurnace furnace = (BlockEntityFurnace) BlockEntities.createBlockEntity(getBlockEntityType(), this.getChunk(), nbt);
        return furnace != null;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
            BlockEntityFurnace furnace;
            if (blockEntity instanceof BlockEntityFurnace) {
                furnace = (BlockEntityFurnace) blockEntity;
            } else {
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, getBlockEntityId())
                        .putList(new ListTag<>("Items"));
                furnace = (BlockEntityFurnace) BlockEntities.createBlockEntity(getBlockEntityType(), getChunk(), nbt);
                if (furnace == null) {
                    return true;
                }
            }

            if (furnace.namedTag.contains("Lock") && furnace.namedTag.get("Lock") instanceof StringTag) {
                if (!furnace.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(furnace.getInventory());
        }

        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId(FURNACE));
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
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityFurnace) {
            return ContainerInventory.calculateRedstone(((BlockEntityFurnace) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public int getDropExp() {
        BlockEntityFurnace furnace = getBlockEntity();
        if (furnace == null) {
            return 0;
        }
        return furnace.getDropXp();
    }

    @Override
    public boolean isFurnace() {
        return true;
    }

    @Nullable
    protected BlockEntityFurnace getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityFurnace furnace) {
            return furnace;
        }
        return null;
    }

    public String getBlockEntityId() {
        return BlockEntity.FURNACE;
    }
}
