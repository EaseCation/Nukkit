package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

/**
 * @author Nukkit Project Team
 */
public class BlockFlowerPot extends BlockFlowable {

    public BlockFlowerPot() {
        this(0);
    }

    public BlockFlowerPot(int meta) {
        super(meta);
    }

    protected static boolean canPlaceIntoFlowerPot(Block block) {
        if (block == null || block.isAir()) {
            return false;
        }

        switch (block.getId()) {
            case SAPLING:
            case DEADBUSH:
            case DANDELION:
            case RED_FLOWER:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case CACTUS:
            case BAMBOO:
            case WITHER_ROSE:
            case CRIMSON_ROOTS:
            case WARPED_ROOTS:
            case CRIMSON_FUNGUS:
            case WARPED_FUNGUS:
            case AZALEA:
            case FLOWERING_AZALEA:
            case MANGROVE_PROPAGULE:
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Flower Pot";
    }

    @Override
    public int getId() {
        return BLOCK_FLOWER_POT;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.FLOWER_POT;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!SupportType.hasCenterSupport(down(), BlockFace.UP)) {
            return false;
        }

        setDamage(0);
        this.getLevel().setBlock(block, this, true, true);
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntityFlowerPot blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        Block block = item.getBlockUnsafe();

        if (blockEntity.hasPlant()) {
            if (!canPlaceIntoFlowerPot(block)) {
                if (player != null) {
                    player.getInventory().addItemOrDrop(blockEntity.getPlant().toItem());
                }

                blockEntity.setPlant(null);

                this.setDamage(0);
                this.level.setBlock(this, this, true);
                blockEntity.spawnToAll();
                return true;
            }
            return false;
        }

        if (!canPlaceIntoFlowerPot(block)) {
            return false;
        }

        blockEntity.setPlant(block.clone());

        this.setDamage(1);
        this.getLevel().setBlock(this, this, true);
        blockEntity.spawnToAll();

        if (player != null && player.isSurvivalLike()) {
            item.pop();
            player.getInventory().setItemInHand(item);
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        BlockEntityFlowerPot blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity.hasPlant()) {
            return new Item[]{
                    Item.get(Item.FLOWER_POT),
                    blockEntity.getPlant().toItem(),
            };
        }

        return new Item[]{
                Item.get(Item.FLOWER_POT),
        };
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.3125;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.3125;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6875;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6875;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.FLOWER_POT);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!SupportType.hasCenterSupport(down(), BlockFace.UP)) {
                this.level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    private BlockEntityFlowerPot createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.FLOWER_POT);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityFlowerPot) BlockEntities.createBlockEntity(BlockEntityType.FLOWER_POT, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityFlowerPot getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityFlowerPot)) {
            return null;
        }
        return (BlockEntityFlowerPot) blockEntity;
    }
}
