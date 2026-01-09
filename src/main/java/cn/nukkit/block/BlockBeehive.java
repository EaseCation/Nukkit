package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

public class BlockBeehive extends BlockSolid implements Faceable {

    public static final int MAX_HONEY_LEVEL = 5;

    public static final int DIRECTION_MASK = 0b11;
    public static final int HONEY_LEVEL_MASK = 0b11100;
    public static final int HONEY_LEVEL_OFFSET = 2;

    BlockBeehive() {

    }

    @Override
    public int getId() {
        return BEEHIVE;
    }

    @Override
    public String getName() {
        return "Beehive";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BEEHIVE;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getItemMeta());
    }

    @Override
    public int getItemKeepMetaMask() {
        return HONEY_LEVEL_MASK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.getId() == ItemID.GLASS_BOTTLE) {
            int honeyLevel = getHoneyLevel();
            if (honeyLevel != MAX_HONEY_LEVEL) {
                return false;
            }

            setHoneyLevel(0);
            level.setBlock(this, this, true, true);

            if (player != null) {
                if (!player.isCreative()) {
                    item.count--;
                }

                for (Item drop : player.getInventory().addItem(Item.get(ItemID.HONEY_BOTTLE))) {
                    player.dropItem(drop);
                }
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
            return true;
        }

        if (item.isShears()) {
            int honeyLevel = getHoneyLevel();
            if (honeyLevel != MAX_HONEY_LEVEL) {
                return false;
            }

            setHoneyLevel(0);
            level.setBlock(this, this, true, true);

            if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                level.dropItem(blockCenter(), Item.get(Item.HONEYCOMB, 0, 3));
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_BEEHIVE_SHEAR);

            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            this.setDamage((getHoneyLevel() << HONEY_LEVEL_OFFSET) | player.getHorizontalFacing().getOpposite().getHorizontalIndex());
        }
        level.setBlock(this, this, true);
        createBlockEntity(item);
        return true;
    }

    @Override
    public int getFuelTime() {
        if (true || V1_21_40.isAvailable()) {
            return 0;
        }
        return 300;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    @Override
    public boolean isBeehive() {
        return true;
    }

    protected BlockEntityBeehive createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BEEHIVE);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityBeehive) BlockEntities.createBlockEntity(BlockEntityType.BEEHIVE, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityBeehive getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityBeehive) {
            return (BlockEntityBeehive) blockEntity;
        }
        return null;
    }

    /**
     * @return 0-5
     */
    public int getHoneyLevel() {
        return getDamage() >> HONEY_LEVEL_OFFSET;
    }

    public void setHoneyLevel(int honeyLevel) {
        setDamage((Mth.clamp(honeyLevel, 0, MAX_HONEY_LEVEL) << HONEY_LEVEL_OFFSET) | (getDamage() & DIRECTION_MASK));
    }
}
