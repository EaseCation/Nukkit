package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.particle.BoneMealParticle;
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
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
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
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            int count = 0;
            for (BlockFace face : BlockFace.getValues()) {
                if (hasFace(face)) {
                    count++;
                }
            }
            if (count <= 0) {
                return new Item[0];
            }

            Item drop = toItem(true);
            drop.setCount(count);
            return new Item[]{drop};
        }
        return new Item[0];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
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
}
