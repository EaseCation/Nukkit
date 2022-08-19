package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockTNT extends BlockSolidMeta {

    public BlockTNT() {
        this(0);
    }

    public BlockTNT(int meta) {
        // 0b1 allow_underwater_bit
        // 0b10 explode_bit
        super(meta & 0b11);
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
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
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

        double mot = (new NukkitRandom()).nextSignedFloat() * Math.PI * 2;
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", this.x + 0.5))
                        .add(new DoubleTag("", this.y))
                        .add(new DoubleTag("", this.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Mth.sin(mot) * 0.02))
                        .add(new DoubleTag("", 0.2))
                        .add(new DoubleTag("", -Mth.cos(mot) * 0.02)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putShort("Fuse", fuse);
        Entity tnt = Entity.createEntity("PrimedTnt",
                this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
                nbt, source
        );
        if (tnt == null) {
            return;
        }
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (!this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
            return false;
        }

        if (item.getId() == Item.FLINT_AND_STEEL) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }
            this.prime(80, player);
            return true;
        } else if (item.getId() == Item.FIRE_CHARGE) {
            if (player != null && !player.isCreative()) item.count--;
            this.prime(80, player);
            return true;
        } else if (item.hasEnchantment(Enchantment.ID_FIRE_ASPECT)) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
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
}
