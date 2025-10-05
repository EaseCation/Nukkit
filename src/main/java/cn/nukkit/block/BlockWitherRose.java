package cn.nukkit.block;

import cn.nukkit.Difficulty;
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
    public boolean isStackedByData() {
        return false;
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
        if (entity.getServer().getDifficulty() == Difficulty.PEACEFUL.ordinal()) {
            return;
        }
        if (!(entity instanceof EntityLiving) || entity instanceof Player && ((Player) entity).isCreative()) {
            return;
        }
        Effect wither = entity.getEffect(Effect.WITHER);
        if (wither != null && (wither.getDuration() > 30 || wither.isInfinite())) {
            return;
        }
        entity.addEffect(Effect.getEffect(Effect.WITHER).setDuration(40 + 1));
    }
}
