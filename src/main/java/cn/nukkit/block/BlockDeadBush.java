package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDeadBush extends BlockFlowable {
    public BlockDeadBush() {
        this(0);
    }

    public BlockDeadBush(int meta) {
        // Dead bushes can't have meta. Also stops the server from throwing an exception with the block palette.
        super(0);
    }

    @Override
    public String getName() {
        return "Dead Bush";
    }

    @Override
    public int getId() {
        return DEADBUSH;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (canSurvive()) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this, true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[]{
                    Item.get(Item.STICK, 0, ThreadLocalRandom.current().nextInt(3))
            };
        }
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
    public int getFuelTime() {
        return 100;
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
    public boolean isPottable() {
        return true;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean canSurvive() {
        Block below = down();
        int id = below.getId();
        return id == SAND || id == RED_SAND || below.isTerracotta() || id == DIRT || id == COARSE_DIRT || id == PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MUD || id == GRASS_BLOCK || id == MUDDY_MANGROVE_ROOTS || id == SUSPICIOUS_SAND;
    }
}
