package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockIceFrosted extends BlockTransparent {

    public BlockIceFrosted() {
        this(0);
    }

    public BlockIceFrosted(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FROSTED_ICE;
    }

    @Override
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public float getFrictionFactor() {
        return 0.98f;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(AIR);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (level.getDimension() == Dimension.NETHER || player != null && player.isCreative() || !item.is(Item.ENCHANTED_BOOK) && item.hasEnchantment(Enchantment.SILK_TOUCH)) {
            return super.onBreak(item, player);
        }
        return level.setBlock(this, Block.get(BlockID.WATER), true);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(20, 20 * 2));
        return true;
    }

    @Override
    public int onUpdate(int type) {
//        if (type == Level.BLOCK_UPDATE_NORMAL) {
//            if (equalsVec(neighborPos) && pos.countNeighbors() < 2) {
//                level.setBlock(this, get(WATER), true);
//            }
//            return type;
//        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if ((ThreadLocalRandom.current().nextInt(3) == 0 || countNeighbors() < 4) /*&& level.getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) > 11*/) { //TODO: light
                slightlyMelt(true);
            } else {
                level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(20, 20 * 2));
            }
            return type;
        }

        return 0;
    }

    private void slightlyMelt(boolean propagate) {
        int age = getDamage();
        if (age < 3) {
            setDamage(age + 1);
            level.setBlock(this, this, true);
            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(20, 20 * 2));
            return;
        }

        level.setBlock(this, get(WATER), true);

        if (!propagate) {
            return;
        }
        for (BlockFace face : BlockFace.getValues()) {
            Block block = getSide(face);
            if (block instanceof BlockIceFrosted) {
                ((BlockIceFrosted) block).slightlyMelt(false);
            }
        }
    }

    private int countNeighbors() {
        int result = 0;
        for (BlockFace face : BlockFace.getValues()) {
            if (getSide(face).getId() == FROSTED_ICE && ++result >= 4) {
                return result;
            }
        }
        return result;
    }
}
