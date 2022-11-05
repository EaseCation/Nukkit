package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 28.01.2016.
 */
public class BlockHugeMushroomRed extends BlockSolidMeta {

    public BlockHugeMushroomRed() {
        this(0);
    }

    public BlockHugeMushroomRed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Red Mushroom Block";
    }

    @Override
    public int getId() {
        return RED_MUSHROOM_BLOCK;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
    public Item[] getDrops(Item item) {
        int count = ThreadLocalRandom.current().nextInt(-7, 3);
        if (count <= 0) {
            return new Item[0];
        }
        return new Item[]{
                new ItemBlock(Block.get(BlockID.RED_MUSHROOM), 0, count),
        };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
