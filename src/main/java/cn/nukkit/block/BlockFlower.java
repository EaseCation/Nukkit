package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFlower extends BlockFlowable {
    public static final int TYPE_POPPY = 0;
    public static final int TYPE_BLUE_ORCHID = 1;
    public static final int TYPE_ALLIUM = 2;
    public static final int TYPE_AZURE_BLUET = 3;
    public static final int TYPE_RED_TULIP = 4;
    public static final int TYPE_ORANGE_TULIP = 5;
    public static final int TYPE_WHITE_TULIP = 6;
    public static final int TYPE_PINK_TULIP = 7;
    public static final int TYPE_OXEYE_DAISY = 8;
    public static final int TYPE_CORNFLOWER = 9;
    public static final int TYPE_LILY_OF_THE_VALLEY = 10;

    private static final String[] NAMES = new String[]{
            "Poppy",
            "Blue Orchid",
            "Allium",
            "Azure Bluet",
            "Red Tulip",
            "Orange Tulip",
            "White Tulip",
            "Pink Tulip",
            "Oxeye Daisy",
            "Cornflower",
            "Lily of the Valley",
            "Flower",
            "Flower",
            "Flower",
            "Flower",
            "Flower",
    };

    public BlockFlower() {
        this(0);
    }

    public BlockFlower(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_FLOWER;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x0f];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canSurvive()) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
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
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 8; i++) {
                Vector3 vec = this.add(
                        random.nextInt(-3, 4),
                        random.nextInt(-1, 2),
                        random.nextInt(-3, 4));

                if (level.getBlock(vec).getId() == AIR && level.getBlock(vec.down()).getId() == GRASS && vec.getY() >= 0 && vec.getY() < 256) {
                    if (random.nextInt(10) == 0) {
                        this.level.setBlock(vec, this.getUncommonFlower(), true);
                    } else {
                        this.level.setBlock(vec, get(this.getId()), true);
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canContainSnow() {
        return true;
    }

    protected Block getUncommonFlower() {
        return get(YELLOW_FLOWER);
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected boolean canSurvive() {
        int id = down().getId();
        return id == Block.GRASS || id == Block.DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK;
    }
}
