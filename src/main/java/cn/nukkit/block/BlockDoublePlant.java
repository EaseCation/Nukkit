package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
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
            "Double Tallgrass",
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
            int meta = getDamage();
            if ((meta & TOP_HALF_BITMASK) == TOP_HALF_BITMASK) {
                Block below = down();
                if (below.getId() != DOUBLE_PLANT || (below.getDamage() & TYPE_MASK) != (meta & TYPE_MASK)) {
                    this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                Block up = this.up();
                if (!canSurvive() || up.getId() != DOUBLE_PLANT || (up.getDamage() & TYPE_MASK) != (meta & TYPE_MASK)) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        Block up = up();
        if (up.canBeReplaced()) {
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up, Block.get(BlockID.DOUBLE_PLANT, getDamage() | TOP_HALF_BITMASK), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if ((this.getDamage() & TOP_HALF_BITMASK) == TOP_HALF_BITMASK) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if ((this.getDamage() & TOP_HALF_BITMASK) != TOP_HALF_BITMASK) {
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

            return new Item[]{toItem(true)};
        }

        return new Item[0];
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
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
    public boolean isVegetation() {
        return true;
    }

    public int getPlantType() {
        return getDamage() & TYPE_MASK;
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == PODZOL || id == FARMLAND || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK;
    }
}
