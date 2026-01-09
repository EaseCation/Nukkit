package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCopperGolemStatue;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityCopperGolem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockCopperGolemStatue extends BlockTransparent implements CopperBehavior, Faceable {
    public static final int DIRECTION_MASK = 0b11;

    BlockCopperGolemStatue() {

    }

    @Override
    public int getId() {
        return COPPER_GOLEM_STATUE;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.COPPER_GOLEM_STATUE;
    }

    @Override
    public String getName() {
        return "Copper Golem Statue";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean isSolid() {
        return false;
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
        setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isNull()) {
            BlockEntityCopperGolemStatue blockEntity = getBlockEntity();
            if (blockEntity == null) {
                blockEntity = createBlockEntity(null);
                if (blockEntity == null) {
                    return true;
                }
            }
            blockEntity.changePose();
            return true;
        }

        if (item.isAxe() && !isWaxed() && getCopperAge() == 0) {
            level.setBlock(this, get(AIR), true);
            new EntityCopperGolem(getChunk(), Entity.getDefaultNBT(add(0.5, 0, 0.5)))
                    //TODO: block entity saved entity nbt data
                    .spawnToAll();
            return true;
        }

        return CopperBehavior.use(this, this, item, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            CopperBehavior.randomTick(this, this);
            return type;
        }
        return 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityCopperGolemStatue blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return 1;
        }
        return 1 + blockEntity.getPose();
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean hasCopperBehavior() {
        return true;
    }

    @Override
    public int getCopperAge() {
        return 0;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDewaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    @Override
    public boolean isCopperGolemStatue() {
        return true;
    }

    protected BlockEntityCopperGolemStatue createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.COPPER_GOLEM_STATUE);

        if (item != null) {
            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }

            if (item.hasCustomBlockData()) {
                for (Tag tag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(tag.getName(), tag);
                }
            }
        }

        return (BlockEntityCopperGolemStatue) BlockEntities.createBlockEntity(BlockEntityType.COPPER_GOLEM_STATUE, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityCopperGolemStatue getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityCopperGolemStatue blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
