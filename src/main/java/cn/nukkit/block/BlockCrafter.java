package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockCrafter extends BlockSolid implements Faceable {
    public static final int ORIENTATION_MASK = 0b1111;
    public static final int TRIGGERED_BIT = 0b1_0000;
    public static final int CRAFTING_BIT = 0b10_0000;

    public static final int ORIENTATION_DOWN_EAST = 0;
    public static final int ORIENTATION_DOWN_NORTH = 1;
    public static final int ORIENTATION_DOWN_SOUTH = 2;
    public static final int ORIENTATION_DOWN_WEST = 3;
    public static final int ORIENTATION_UP_EAST = 4;
    public static final int ORIENTATION_UP_NORTH = 5;
    public static final int ORIENTATION_UP_SOUTH = 6;
    public static final int ORIENTATION_UP_WEST = 7;
    public static final int ORIENTATION_WEST_UP = 8;
    public static final int ORIENTATION_EAST_UP = 9;
    public static final int ORIENTATION_NORTH_UP = 10;
    public static final int ORIENTATION_SOUTH_UP = 11;

    public BlockCrafter() {
    }

    @Override
    public int getId() {
        return CRAFTER;
    }

    @Override
    public String getName() {
        return "Crafter";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CRAFTER;
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 17.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        //TODO: orientation
        return super.place(item, block, target, face, fx, fy, fz, player);
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

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (!level.isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            //TODO
            return type;
        }

        return 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return switch (getOrientation()) {
            case ORIENTATION_DOWN_EAST, ORIENTATION_DOWN_NORTH, ORIENTATION_DOWN_SOUTH, ORIENTATION_DOWN_WEST -> BlockFace.DOWN;
            case ORIENTATION_UP_EAST, ORIENTATION_UP_NORTH, ORIENTATION_UP_SOUTH, ORIENTATION_UP_WEST -> BlockFace.UP;
            case ORIENTATION_WEST_UP -> BlockFace.WEST;
            case ORIENTATION_EAST_UP -> BlockFace.EAST;
            case ORIENTATION_SOUTH_UP -> BlockFace.SOUTH;
            default -> BlockFace.NORTH;
        };
    }

    protected BlockEntityCrafter createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CRAFTER);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityCrafter) BlockEntities.createBlockEntity(BlockEntityType.CRAFTER, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityCrafter getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityCrafter blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public int getOrientation() {
        return getDamage() & ORIENTATION_MASK;
    }

    public boolean isTriggered() {
        return (getDamage() & TRIGGERED_BIT) != 0;
    }

    public void setTriggered(boolean triggered) {
        setDamage(triggered ? getDamage() | TRIGGERED_BIT : getDamage() & ~TRIGGERED_BIT);
    }

    public boolean isCrafting() {
        return (getDamage() & CRAFTING_BIT) != 0;
    }

    public void setCrafting(boolean crafting) {
        setDamage(crafting ? getDamage() | CRAFTING_BIT : getDamage() & ~CRAFTING_BIT);
    }
}
