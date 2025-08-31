package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.block.BlockToolType;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChemistryTable;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import javax.annotation.Nullable;

public class BlockChemistryTable extends BlockSolid {

    public static final int DIRECTION_MASK = 0b11;
    public static final int DIRECTION_BITS = 2;

    public static final int TYPE_COMPOUND_CREATOR = 0;
    public static final int TYPE_MATERIAL_REDUCER = 1 << DIRECTION_BITS;
    public static final int TYPE_ELEMENT_CONSTRUCTOR = 2 << DIRECTION_BITS;
    public static final int TYPE_LAB_TABLE = 3 << DIRECTION_BITS;

    private static final String[] NAMES = {
            "Compound Creator",
            "Material Reducer",
            "Element constructor",
            "Lab Table",
    };

    public BlockChemistryTable() {
        this(0);
    }

    public BlockChemistryTable(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHEMISTRY_TABLE;
    }

    @Override
    public String getName() {
        return NAMES[(getDamage() & 0xf) >> DIRECTION_BITS];
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CHEMISTRY_TABLE;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public float getHardness() {
        return 2.5f;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), (getDamage() >> DIRECTION_BITS) << DIRECTION_BITS);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            setDamage(((getDamage() >> DIRECTION_BITS) << DIRECTION_BITS) | player.getHorizontalFacing().getHorizontalIndex());
        }
        level.setBlock(this, this, true);
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        //TODO: UI
        return true;
    }

    protected BlockEntityChemistryTable createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHEMISTRY_TABLE);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityChemistryTable) BlockEntities.createBlockEntity(BlockEntityType.CHEMISTRY_TABLE, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityChemistryTable getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityChemistryTable) {
            return (BlockEntityChemistryTable) blockEntity;
        }
        return null;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
