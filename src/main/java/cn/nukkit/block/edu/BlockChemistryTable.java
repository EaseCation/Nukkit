package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSolidMeta;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChemistryTable;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

public class BlockChemistryTable extends BlockSolidMeta {

    public static final int DIRECTION_MASK = 0b11;
    public static final int DIRECTION_BITS = 2;

    public static final int COMPOUND_CREATOR = 0;
    public static final int MATERIAL_REDUCER = 1 << DIRECTION_BITS;
    public static final int ELEMENT_CONSTRUCTOR = 2 << DIRECTION_BITS;
    public static final int LAB_TABLE = 3 << DIRECTION_BITS;

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
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId(), (getDamage() >> DIRECTION_BITS) << DIRECTION_BITS);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        //TODO: UI
        return true;
    }

    protected BlockEntityChemistryTable createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHEMISTRY_TABLE);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
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
