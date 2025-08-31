package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockSweetBerryBush extends BlockFlowable {

    public static final int STAGE_SAPLING = 0;
    public static final int STAGE_BUSH_NO_BERRIES = 1;
    public static final int STAGE_BUSH_SOME_BERRIES = 2;
    public static final int STAGE_MATURE = 3;

    public BlockSweetBerryBush() {
        this(0);
    }

    public BlockSweetBerryBush(int meta) {
        super(meta & 0x7);
    }

    @Override
    public String getName() {
        return "Sweet Berry Bush";
    }

    @Override
    public int getId() {
        return SWEET_BERRY_BUSH;
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 0;
        }
        return 0.2f;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.SWEET_BERRIES);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        int meta = getDamage() & 0x7;
        if (meta <= STAGE_BUSH_NO_BERRIES) {
            return new Item[0];
        }

        return new Item[]{
                Item.get(ItemID.SWEET_BERRIES, 0, getBerryDropAmount(meta)),
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!this.canSurvive()) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        int meta = getDamage() & 0x7;
        if (meta < STAGE_MATURE && item.isFertilizer()) {
            Block newBlock = this.clone();
            newBlock.setDamage(meta + 1);

            BlockGrowEvent event = new BlockGrowEvent(this, newBlock);
            event.call();
            if (!event.isCancelled()) {
                this.level.setBlock(this, event.getNewState(), true);
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }

        if (meta <= STAGE_BUSH_NO_BERRIES) {
            return false;
        }

        level.addLevelSoundEvent(this.add(0.5, 0, 0.5), LevelSoundEventPacket.SOUND_BLOCK_SWEET_BERRY_BUSH_PICK);

        this.setDamage(STAGE_BUSH_NO_BERRIES);
        this.level.setBlock(this, this, true);

        if (this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
            this.level.dropItem(this, Item.get(ItemID.SWEET_BERRIES, 0, getBerryDropAmount(meta)));
        }
        return true;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.canSurvive()) {
                this.level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            int meta = this.getDamage() & 0x7;
            if (meta < STAGE_MATURE && ThreadLocalRandom.current().nextInt(5) == 0) {
                Block newBlock = this.clone();
                newBlock.setDamage(meta + 1);

                BlockGrowEvent event = new BlockGrowEvent(this, newBlock);
                event.call();
                if (!event.isCancelled()) {
                    this.level.setBlock(this, event.getNewState(), true);
                }

                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if ((getDamage() & 0x7) == STAGE_SAPLING) {
            return;
        }

        entity.resetFallDistance();

        //TODO
        /*if (entity instanceof EntityLiving && !(entity.riding instanceof EntityVehicle)
                && (Math.abs(entity.motionX) > 0.003 || Math.abs(entity.motionZ) > 0.003)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1));
        }*/
    }

    private boolean canSurvive() {
        int below = down().getId();
        return below == Block.GRASS_BLOCK || below == Block.DIRT || below == COARSE_DIRT || below == Block.PODZOL || below == MYCELIUM || below == DIRT_WITH_ROOTS || below == MOSS_BLOCK || below == PALE_MOSS_BLOCK || below == FARMLAND || below == MUD || below == MUDDY_MANGROVE_ROOTS;
    }

    private int getBerryDropAmount(int meta) {
        if (meta >= STAGE_MATURE) {
            return ThreadLocalRandom.current().nextInt(2, 4);
        }
        if (meta == STAGE_BUSH_SOME_BERRIES) {
            return ThreadLocalRandom.current().nextInt(1, 3);
        }
        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
