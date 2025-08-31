package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

/**
 * author: Justin
 */
public class BlockSkull extends BlockFlowable implements Faceable {

    public BlockSkull() {
        this(0);
    }

    public BlockSkull(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_SKULL;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SKULL;
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public String getName() {
        /*
        int itemMeta = 0;

        if (this.level != null) {
            BlockEntity blockEntity = getLevel().getBlockEntity(this);
            if (blockEntity != null) itemMeta = blockEntity.namedTag.getByte("SkullType");
        }

        return ItemSkull.getItemSkullName(itemMeta);
        */
        return V1_21_40.isAvailable() ? "Skeleton Skull" : "Skull";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        switch (face) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case UP:
                this.setDamage(face.getIndex());
                break;
            case DOWN:
            default:
                return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SKULL)
                .putByte("SkullType", V1_21_40.isAvailable() ? -1 : item.getDamage())
                .putByte("Rot", Mth.floor((player.yaw * 16 / 360) + 0.5) & 0x0f);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        BlockEntitySkull skull = (BlockEntitySkull) BlockEntities.createBlockEntity(BlockEntityType.SKULL, getChunk(), nbt);
        if (skull == null) {
            return false;
        }

        // TODO: 2016/2/3 SPAWN WITHER

        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public Item toItem(boolean addUserData) {
        if (V1_21_40.isAvailable()) {
            return Item.get(getItemId());
        }

        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        int itemMeta = ItemSkull.HEAD_SKELETON;
        if (blockEntity != null) {
            itemMeta = blockEntity.namedTag.getByte("SkullType");
        }
        return Item.get(Item.SKULL, itemMeta);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN && type == SupportType.CENTER && getBlockFace() == BlockFace.UP;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(this.x + 0.25, this.y, this.z + 0.25, this.x + 1 - 0.25, this.y + 0.5, this.z + 1 - 0.25);
        switch (this.getBlockFace()) {
            case NORTH:
                return bb.offset(0, 0.25, 0.25);
            case SOUTH:
                return bb.offset(0, 0.25, -0.25);
            case WEST:
                return bb.offset(0.25, 0.25, 0);
            case EAST:
                return bb.offset(-0.25, 0.25, 0);
        }
        return bb;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSkull() {
        return true;
    }

    @Nullable
    protected BlockEntitySkull getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntitySkull blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
