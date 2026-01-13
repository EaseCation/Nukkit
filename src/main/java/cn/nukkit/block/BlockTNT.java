package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockTNT extends BlockSolid {
    public static final int[] TNTS = {
            TNT,
            UNDERWATER_TNT,
    };

    public static final int EXPLODE_BIT = 0b1;
    @Deprecated
    public static final int ALLOW_UNDERWATER_BIT = 0b10;

    protected BlockTNT() {

    }

    @Override
    public String getName() {
        return "TNT";
    }

    @Override
    public int getId() {
        return TNT;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    public void prime() {
        this.prime(80);
    }

    public void prime(int fuse) {
        prime(fuse, null);
    }

    public void prime(int fuse, Entity source) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);

        if (!this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
            return;
        }

        float mot = ThreadLocalRandom.current().nextFloat() * Mth.PI * 2;
        CompoundTag nbt = Entity.getDefaultNBT(x + 0.5, y, z + 0.5, -Mth.sin(mot) * 0.02, 0.2, -Mth.cos(mot) * 0.02)
                .putByte("Fuse", fuse)
                .putBoolean("AllowUnderwater", isAllowUnderwater());
        Entity tnt = new EntityPrimedTNT(
                this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
                nbt, source
        );
        tnt.spawnToAll();
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_TNT);
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && this.level.isBlockPowered(this)) {
            this.prime();
        }

        return 0;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
            return false;
        }

        if (item.getId() == Item.FLINT_AND_STEEL || item.isSword() && item.hasEnchantment(Enchantment.FIRE_ASPECT)) {
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            this.prime(80, player);
            return true;
        }
        if (item.getId() == Item.FIRE_CHARGE) {
            level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_GHAST_SHOOT, 78642);

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            this.prime(80, player);
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FIRE_BLOCK_COLOR;
    }

    protected boolean isAllowUnderwater() {
        return false;
    }
}
