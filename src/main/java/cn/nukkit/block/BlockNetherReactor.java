package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityNetherReactor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

public class BlockNetherReactor extends BlockSolid {

    public BlockNetherReactor() {

    }

    @Override
    public String getName() {
        return "Nether Reactor Core";
    }

    @Override
    public int getId() {
        return NETHERREACTOR;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.NETHER_REACTOR;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(Item.DIAMOND, 0, 3),
                    Item.get(Item.IRON_INGOT, 0, 6)
            };
        }
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId());
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    protected BlockEntityNetherReactor createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.NETHER_REACTOR);

        return (BlockEntityNetherReactor) BlockEntities.createBlockEntity(BlockEntityType.NETHER_REACTOR, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityNetherReactor getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityNetherReactor) {
            return (BlockEntityNetherReactor) blockEntity;
        }
        return null;
    }
}
