package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDecoratedPot;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BlockDecoratedPot extends BlockTransparent {
    public static final int DIRECTION_MASK = 0b11;

    BlockDecoratedPot() {

    }

    @Override
    public int getId() {
        return DECORATED_POT;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.DECORATED_POT;
    }

    @Override
    public String getName() {
        return "Decorated Pot";
    }

    @Override
    public float getHardness() {
        return 0f;
    }

    @Override
    public float getResistance() {
        return 0f;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, @Nullable Player player) {
        if (!item.isPickaxe() && !item.isAxe() && !item.isSword() && !item.isShovel() && !item.isHoe()) {
            return super.getDrops(item, player);
        }

        BlockEntityDecoratedPot blockEntity = getBlockEntity();
        String[] sherds;
        if (blockEntity == null || (sherds = blockEntity.getSherds()) == null) {
            return new Item[]{
                    Item.get(Item.BRICK, 0, 4)
            };
        }

        Map<String, Item> items = new HashMap<>();
        for (String sherd : sherds) {
            items.computeIfAbsent(sherd, name -> Item.get(Items.getIdByName(name), 0, 0)).grow(1);
        }
        return items.values().toArray(new Item[0]);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex());
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
        if (player == null) {
            return false;
        }

        BlockEntityDecoratedPot blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        boolean success = false;
        Item slot;
        if (item.hasLock()) {
            slot = null;
        } else if ((slot = blockEntity.getItem()) == null) {
            if (!item.isNull()) {
                success = true;

                slot = item.clone();
                slot.setCount(1);

                if (player.isSurvivalLike()) {
                    item.pop();
                }
            }
        } else if (slot.getCount() < slot.getMaxStackSize() && slot.equals(item)) {
            success = true;

            slot.grow(1);

            if (player.isSurvivalLike()) {
                item.pop();
            }
        }

        if (success) {
            blockEntity.setItem(slot);

            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_DUST_PLUME, new CompoundTag()
                    .putFloat("x", (float) x + 0.5f)
                    .putFloat("y", (float) y + 1.25f)
                    .putFloat("z", (float) z + 0.5f));
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_DECORATED_POT_INSERT, 700 + Mth.floor(7.8125f * slot.getCount() * (64f / slot.getMaxStackSize())));
            blockEntity.playAnimation(BlockEntityDecoratedPot.ANIMATION_INSERT_SUCCESS);
        } else {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_DECORATED_POT_INSERT_FAILED);
            blockEntity.playAnimation(BlockEntityDecoratedPot.ANIMATION_INSERT_FAIL);
        }
        return true;
    }

    @Override
    public void onProjectileHit(EntityProjectile projectile, MovingObjectPosition hitResult) {
        if (!level.gameRules.getBoolean(GameRule.PROJECTILES_CAN_BREAK_BLOCKS)) {
            return;
        }
        level.useBreakOn(this, LazyHolder.TOOL_ITEM, true);
    }

    protected BlockEntityDecoratedPot createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.DECORATED_POT);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityDecoratedPot) BlockEntities.createBlockEntity(BlockEntityType.DECORATED_POT, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityDecoratedPot getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityDecoratedPot blockEntity) {
            return blockEntity;
        }
        return null;
    }

    private static class LazyHolder {
        private static final Item TOOL_ITEM = Item.get(Item.WOODEN_SWORD);
    }
}
