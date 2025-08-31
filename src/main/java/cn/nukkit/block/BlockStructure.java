package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityStructureBlock;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockStructure extends BlockSolid {

    public static final int TYPE_DATA = 0;
    public static final int TYPE_SAVE = 1;
    public static final int TYPE_LOAD = 2;
    public static final int TYPE_CORNER = 3;
    public static final int TYPE_INVALID = 4;
    public static final int TYPE_EXPORT = 5;

    public BlockStructure() {
        this(0);
    }

    public BlockStructure(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRUCTURE_BLOCK;
    }

    @Override
    public String getName() {
        return "Structure Block";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.STRUCTURE_BLOCK;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }

        setDamage(TYPE_SAVE);
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
        if (player == null || !player.isCreative() || !player.isOp()) {
            return true;
        }
        //TODO: UI
        //player.openBlockEditor(getFloorX(), getFloorY(), getFloorZ(), ContainerType.STRUCTURE_EDITOR);
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    protected BlockEntityStructureBlock createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.STRUCTURE_BLOCK);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityStructureBlock) BlockEntities.createBlockEntity(BlockEntityType.STRUCTURE_BLOCK, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityStructureBlock getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityStructureBlock) {
            return (BlockEntityStructureBlock) blockEntity;
        }
        return null;
    }
}
