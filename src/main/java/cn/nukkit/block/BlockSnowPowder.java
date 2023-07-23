package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

public class BlockSnowPowder extends BlockFlowable {
    public BlockSnowPowder() {
        this(0);
    }

    public BlockSnowPowder(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return POWDER_SNOW;
    }

    @Override
    public String getName() {
        return "Powder Snow";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BUCKET, ItemBucket.POWDER_SNOW_BUCKET);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean breaksWhenMoved() {
        return false;
    }

    @Override
    public boolean sticksToPiston() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
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
        entity.resetFallDistance();

        if (entity.isOnFire() && (entity instanceof EntityLiving || entity instanceof EntityProjectile)) {
            level.addLevelSoundEvent(entity.upVec(), LevelSoundEventPacket.SOUND_FIZZ);
            level.addLevelEvent(entity, LevelEventPacket.EVENT_PARTICLE_FIZZ_EFFECT, 513);
            entity.extinguish();

            if (entity instanceof Player || level.gameRules.getBoolean(GameRule.MOB_GRIEFING)) {
                level.useBreakOn(this, true);
            }

            entity.resetFrozenState();
            return;
        }

        if (entity instanceof Player) {
            PlayerInventory inventory = ((Player) entity).getInventory();
            for (int i = 0; i < 4; i++) {
                if (inventory.getArmorItem(i).getTier() == ItemArmor.TIER_LEATHER) {
                    return;
                }
            }
        }

        entity.freezing = true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
