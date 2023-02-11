package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.potion.Effect;

public class BlockWitherRose extends BlockFlower {

    public BlockWitherRose() {
        this(0);
    }

    public BlockWitherRose(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return WITHER_ROSE;
    }

    @Override
    public String getName() {
        return "Wither Rose";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public int getFullId() {
        return this.getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    protected Block getUncommonFlower() {
        return this;
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
        if (!(entity instanceof EntityLiving) || entity instanceof Player && ((Player) entity).isCreative()) {
            return;
        }
        Effect wither = entity.getEffect(Effect.WITHER);
        if (wither != null && wither.getDuration() > 1) {
            return;
        }
        entity.addEffect(Effect.getEffect(Effect.WITHER).setDuration(40 + 1));
    }
}
