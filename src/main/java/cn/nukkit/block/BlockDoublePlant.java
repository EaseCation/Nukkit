package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoublePlant extends BlockFlowable {
    public static final int TYPE_SUNFLOWER = 0;
    public static final int TYPE_LILAC = 1;
    public static final int TYPE_TALL_GRASS = 2;
    public static final int TYPE_LARGE_FERN = 3;
    public static final int TYPE_ROSE_BUSH = 4;
    public static final int TYPE_PEONY = 5;

    public static final int TYPE_MASK = 0b111;
    public static final int TOP_HALF_BITMASK = 0x8;

    private static final String[] NAMES = new String[]{
            "Sunflower",
            "Lilac",
            "Tall Grass",
            "Large Fern",
            "Rose Bush",
            "Peony",
            "Double Plant",
            "Double Plant",
    };

    public BlockDoublePlant() {
        this(0);
    }

    public BlockDoublePlant(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_PLANT;
    }

    @Override
    public boolean canBeReplaced() {
        int type = getPlantType();
        return type == TYPE_TALL_GRASS || type == TYPE_LARGE_FERN;
    }

    @Override
    public String getName() {
        return NAMES[this.getPlantType()];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int upperBlockBitMask = getUpperBlockBitMask();
            int typeMask = getPlantTypeMask();
            int meta = getDamage();
            if ((meta & upperBlockBitMask) == upperBlockBitMask) {
                Block below = down();
                if (below.getId() != getId() || (below.getDamage() & typeMask) != (meta & typeMask)) {
                    this.getLevel().useBreakOn(this, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                Block up = this.up();
                if (!canSurvive() || up.getId() != getId() || (up.getDamage() & typeMask) != (meta & typeMask)) {
                    this.getLevel().useBreakOn(this, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        if (!level.getHeightRange().isValidBlockY(getFloorY() + 1)) {
            return false;
        }

        Block up = up();
        if (up.canBeReplaced()) {
            int upperBlockBitMask = getUpperBlockBitMask();
            int lowerMeta = getDamage() & ~upperBlockBitMask;
            setDamage(lowerMeta);
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up, Block.get(getId(), lowerMeta | upperBlockBitMask), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        int upperBlockBitMask = getUpperBlockBitMask();
        int typeMask = getPlantTypeMask();
        if ((this.getDamage() & upperBlockBitMask) == upperBlockBitMask) { // Top half
            Block down = down();
            if (down.getId() == getId() && (down.getDamage() & typeMask) == (getDamage() & typeMask)) {
                level.addParticle(new DestroyBlockParticle(down, down));
                this.getLevel().setBlock(down, Block.get(BlockID.AIR), true, true);
            }
        } else {
            Block above = up();
            if (above.getId() == getId() && (above.getDamage() & typeMask) == (getDamage() & typeMask)) {
                level.addParticle(new DestroyBlockParticle(above, above));
                this.getLevel().setBlock(above, Block.get(BlockID.AIR), true, true);
            }
        }
        return super.onBreak(item, player);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        switch (this.getPlantType()) {
            case TYPE_TALL_GRASS:
            case TYPE_LARGE_FERN:
                boolean dropSeeds = ThreadLocalRandom.current().nextInt(10) == 0;
                if (item.isShears()) {
                    if (dropSeeds) {
                        return new Item[]{
                                Item.get(Item.WHEAT_SEEDS),
                                toItem(true)
                        };
                    } else {
                        return new Item[]{
                                toItem(true)
                        };
                    }
                }

                if (dropSeeds) {
                    return new Item[]{
                            Item.get(Item.WHEAT_SEEDS)
                    };
                } else {
                    return new Item[0];
                }
        }
        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getPlantType());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            switch (this.getPlantType()) {
                case TYPE_SUNFLOWER:
                case TYPE_LILAC:
                case TYPE_ROSE_BUSH:
                case TYPE_PEONY:
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }
                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.dropItem(this, this.toItem());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public int getCompostableChance() {
        if (getPlantType() == TYPE_TALL_GRASS) {
            return 50;
        }
        return 65;
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
    public boolean isVegetation() {
        return true;
    }

    public int getPlantType() {
        return getDamage() & TYPE_MASK;
    }

    protected int getPlantTypeMask() {
        return TYPE_MASK;
    }

    protected int getUpperBlockBitMask() {
        return TOP_HALF_BITMASK;
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == PODZOL || id == FARMLAND || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }
}
