package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityVault;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockVault extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int VAULT_STATE_MASK = 0b1100;
    public static final int VAULT_STATE_OFFSET = 2;
    public static final int OMINOUS_BIT = 0b10000;

    public static final int STATE_INACTIVE = 0;
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_UNLOCKING = 2;
    public static final int STATE_EJECTING = 3;

    BlockVault() {

    }

    @Override
    public int getId() {
        return VAULT;
    }

    @Override
    public String getName() {
        return "Vault";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.VAULT;
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
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (getState() != STATE_ACTIVE) {
            return false;
        }

        boolean ominous = isOminous();
        if (ominous) {
            if (!item.is(Item.OMINOUS_TRIAL_KEY)) {
                return true;
            }
        } else if (!item.is(Item.TRIAL_KEY)) {
            return true;
        }

        //TODO
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            //TODO
            return type;
        }

        return 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    protected BlockEntityVault createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.VAULT);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityVault) BlockEntities.createBlockEntity(BlockEntityType.VAULT, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityVault getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityVault blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public int getState() {
        return (getDamage() & VAULT_STATE_MASK) >> VAULT_STATE_OFFSET;
    }

    public void setState(int state) {
        setDamage((getDamage() & ~VAULT_STATE_MASK) | (state << VAULT_STATE_OFFSET));
    }

    public boolean isOminous() {
        return (getDamage() & OMINOUS_BIT) != 0;
    }

    public void setOminous(boolean ominous) {
        setDamage(ominous ? getDamage() | OMINOUS_BIT : getDamage() & ~OMINOUS_BIT);
    }
}
