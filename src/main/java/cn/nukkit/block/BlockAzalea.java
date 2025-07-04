package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.tree.ObjectAzaleaTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

public class BlockAzalea extends BlockFlowable {
    public BlockAzalea() {
        this(0);
    }

    public BlockAzalea(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Azalea";
    }

    @Override
    public int getId() {
        return AZALEA;
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            new ObjectAzaleaTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom.current());
            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!canSurvive()) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 100;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK  || id == CLAY || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }
}
