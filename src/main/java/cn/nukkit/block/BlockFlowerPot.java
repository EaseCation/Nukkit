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

    protected static boolean canPlaceIntoFlowerPot(Item item) {
        Block block = item.getBlockUnsafe();
        if (block == null || block.isAir()) {
            return false;
        }

        switch (block.getId()) {
            case SAPLING:
            case DEADBUSH:
            case YELLOW_FLOWER:
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
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityFlowerPot)) {
            blockEntity = createBlockEntity(null);
        }

        if (blockEntity.namedTag.getShort("item") != AIR || blockEntity.namedTag.getInt("mData") != AIR) {
            if (!canPlaceIntoFlowerPot(item)) {
                int id = blockEntity.namedTag.getShort("item");
                if (id == AIR) id = blockEntity.namedTag.getInt("mData");

                if (player != null) {
                    for (Item drop : player.getInventory().addItem(Item.get(getItemId(id), blockEntity.namedTag.getInt("data")))) {
                        player.dropItem(drop);
                    }
                }

                blockEntity.namedTag.putShort("item", AIR);
                blockEntity.namedTag.putInt("data", 0);

                this.setDamage(0);
                this.level.setBlock(this, this, true);
                ((BlockEntityFlowerPot) blockEntity).spawnToAll();
                return true;
            }
            return false;
        }

        if (!canPlaceIntoFlowerPot(item)) {
            return false;
        }

        blockEntity.namedTag.putShort("item", item.getBlockId());
        blockEntity.namedTag.putInt("data", item.getDamage());

        this.setDamage(1);
        this.getLevel().setBlock(this, this, true);
        ((BlockEntityFlowerPot) blockEntity).spawnToAll();

        if (player != null && player.isSurvival()) {
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        boolean dropInside = false;
        int insideID = 0;
        int insideMeta = 0;
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityFlowerPot) {
            dropInside = true;
            insideID = blockEntity.namedTag.getShort("item");
            insideMeta = blockEntity.namedTag.getInt("data");
        }

        if (dropInside) {
            return new Item[]{
                    Item.get(Item.FLOWER_POT),
                    Item.get(getItemId(insideID), insideMeta, 1)
            };
        } else {
            return new Item[]{
                    Item.get(Item.FLOWER_POT)
            };
        }
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
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!SupportType.hasCenterSupport(down(), BlockFace.UP)) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    private BlockEntityFlowerPot createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.FLOWER_POT)
                .putShort("item", AIR)
                .putInt("data", 0);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityFlowerPot) BlockEntities.createBlockEntity(BlockEntityType.FLOWER_POT, getChunk(), nbt);
    }
}
