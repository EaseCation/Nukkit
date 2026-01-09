package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCreakingHeart;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class BlockCreakingHeart extends BlockRotatedPillar {
    public static final int ACTIVE_BIT = 0b100;
    public static final int NATURAL_BIT = 0b1000;

    BlockCreakingHeart() {

    }

    @Override
    public int getId() {
        return CREAKING_HEART;
    }

    @Override
    public String getName() {
        return "Creaking Heart";
    }

    @Override
    public float getHardness() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(ItemBlockID.RESIN_CLUMP, 0, ThreadLocalRandom.current().nextInt(1, 4)),
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity();
        return true;
    }

    protected BlockEntityCreakingHeart createBlockEntity() {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CREAKING_HEART);

        return (BlockEntityCreakingHeart) BlockEntities.createBlockEntity(BlockEntityType.CREAKING_HEART, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityCreakingHeart getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityCreakingHeart blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
