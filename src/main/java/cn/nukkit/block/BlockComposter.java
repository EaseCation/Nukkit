package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockComposter extends BlockTransparent {

    BlockComposter() {

    }

    @Override
    public String getName() {
        return "Composter";
    }

    @Override
    public int getId() {
        return COMPOSTER;
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 0.6f;
        }
        return 2;
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
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB[] getCollisionShape(int flags) {
        int fillLevel = getDamage();
        int height = switch (fillLevel) {
            case 0 -> 2;
            case 8 -> 15;
            default -> fillLevel * 2 + 1;
        };
        return new AxisAlignedBB[]{
                new SimpleAxisAlignedBB(x, y, z, x + 1, y + height / 16f, z + 1), // bottom
                new SimpleAxisAlignedBB(x, y, z, x + 2 / 16f, y + 1, z + 1), // west
                new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 2 / 16f), // north
                new SimpleAxisAlignedBB(x + 1 - 2 / 16f, y, z, x + 1, y + 1, z + 1), // east
                new SimpleAxisAlignedBB(x, y, z + 1 - 2 / 16f, x + 1, y + 1, z + 1), // south
        };
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return Math.min(getDamage(), 7);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        Item drop = this.toItem(true);
        return getDamage() == 0x8 ? new Item[]{
                drop,
                Item.get(ItemID.BONE_MEAL),
        } : new Item[]{drop};
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (getDamage() == 0x7) {
            level.scheduleUpdate(this, 20);
        }

        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        int meta = getDamage();

        if (meta == 0x7) {
            return true;
        }

        if (meta > 0x7) {
            level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_EMPTY);

            if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                level.dropItem(this, Item.get(ItemID.BONE_MEAL));
            }

            setDamage(0);
            level.setBlock(this, this, true);
            return true;
        }

        int chance = item.getCompostableChance();
        if (chance <= 0) {
            return true;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        level.addLevelEvent(this.add(0, 0.75, 0), LevelEventPacket.EVENT_PARTICLE_BONEMEAL);
        if (chance <= ThreadLocalRandom.current().nextInt(100)) {
            level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_FILL);
            return true;
        }
        level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_FILL_SUCCESS);

        setDamage(++meta);
        level.setBlock(this, this, true);

        if (meta == 0x7) {
            level.scheduleUpdate(this, 20);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
         if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int meta = this.getDamage();
            if (meta == 0x7) {
                level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_READY);

                this.setDamage(meta + 1);
                this.level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_SCHEDULED;
            }
        }
        return 0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face != BlockFace.UP;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
