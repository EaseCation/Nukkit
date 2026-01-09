package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTrialSpawner;
import cn.nukkit.blockentity.BlockEntityTrialSpawner.SpawnData;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSpawnEgg;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;
import java.util.List;

public class BlockTrialSpawner extends BlockTransparent {
    public static final int TRIAL_SPAWNER_STATE_MASK = 0b111;
    public static final int OMINOUS_BIT = 0b1000;

    public static final int STATE_INACTIVE = 0;
    public static final int STATE_WAITING_FOR_PLAYERS = 1;
    public static final int STATE_ACTIVE = 2;
    public static final int STATE_WAITING_FOR_REWARD_EJECTION = 3;
    public static final int STATE_EJECTING_REWARD = 4;
    public static final int STATE_COOLDOWN = 5;

    BlockTrialSpawner() {

    }

    @Override
    public int getId() {
        return TRIAL_SPAWNER;
    }

    @Override
    public String getName() {
        return "Trial Spawner";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.TRIAL_SPAWNER;
    }

    @Override
    public float getHardness() {
        return 50;
    }

    @Override
    public float getResistance() {
        return 250;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
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
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
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
        if (item.isSpawnEgg()) {
            BlockEntityTrialSpawner blockEntity = getBlockEntity();
            if (blockEntity == null) {
                blockEntity = createBlockEntity(null);
                if (blockEntity == null) {
                    return true;
                }
            }

            blockEntity.spawnToAll(true);

            if (getState() == STATE_INACTIVE) {
                setState(STATE_WAITING_FOR_PLAYERS);
                level.setBlock(this, this, true);
            }

            int entityType = ((ItemSpawnEgg) item).getEntityId();

            List<SpawnData> normalSpawnPotentials = blockEntity.getNormalConfig().getSpawnPotentials();
            normalSpawnPotentials.clear();
            SpawnData normalSpawnData = new SpawnData();
            normalSpawnData.setEntityType(entityType);
            normalSpawnPotentials.add(normalSpawnData);

            List<SpawnData> ominousSpawnPotentials = blockEntity.getOminousConfig().getSpawnPotentials();
            ominousSpawnPotentials.clear();
            SpawnData ominousSpawnData = new SpawnData();
            ominousSpawnData.setEntityType(entityType);
            ominousSpawnPotentials.add(ominousSpawnData);

            SpawnData spawnData = new SpawnData();
            spawnData.setEntityType(entityType);
            blockEntity.setSpawnData(spawnData);

            blockEntity.setCooldownEndAt(0);

            blockEntity.spawnToAll();

            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }
            return true;
        }
        return false;
    }

    protected BlockEntityTrialSpawner createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.TRIAL_SPAWNER);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityTrialSpawner) BlockEntities.createBlockEntity(BlockEntityType.TRIAL_SPAWNER, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityTrialSpawner getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityTrialSpawner blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public int getState() {
        return getDamage() & TRIAL_SPAWNER_STATE_MASK;
    }

    public void setState(int state) {
        setDamage((getDamage() & ~TRIAL_SPAWNER_STATE_MASK) | state);
    }

    public boolean isOminous() {
        return (getDamage() & OMINOUS_BIT) != 0;
    }

    public void setOminous(boolean ominous) {
        setDamage(ominous ? getDamage() | OMINOUS_BIT : getDamage() & ~OMINOUS_BIT);
    }
}
