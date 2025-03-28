package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockGlowLichen extends BlockMultiface {
    public BlockGlowLichen() {
        this(0);
    }

    public BlockGlowLichen(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
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
    public int getLightLevel() {
        return 7;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE | BlockToolType.SHEARS | BlockToolType.SWORD;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return super.getDrops(item, player);
        }
        return new Item[0];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        /*
        if (item.isBoneMeal()) {
            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            //TODO
            return true;
        }
        */
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }
}
