package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMobSpawner;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockMobSpawner extends BlockSolid {

    public BlockMobSpawner() {
    }

    @Override
    public String getName() {
        return "Monster Spawner";
    }

    @Override
    public int getId() {
        return MOB_SPAWNER;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.MOB_SPAWNER;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public float getHardness() {
        return 5;
    }

    @Override
    public float getResistance() {
        return 25;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(15, 44);
    }

    @Override
    public boolean canContainWater() {
        return true;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.getId() != ItemID.SPAWN_EGG) {
            return false;
        }

        BlockEntityMobSpawner blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity.setEntityType(item.getDamage()) && player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    protected BlockEntityMobSpawner createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.MOB_SPAWNER);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityMobSpawner) BlockEntities.createBlockEntity(BlockEntityType.MOB_SPAWNER, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityMobSpawner getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityMobSpawner) {
            return (BlockEntityMobSpawner) blockEntity;
        }
        return null;
    }
}
