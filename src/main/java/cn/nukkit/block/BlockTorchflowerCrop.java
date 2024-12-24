package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

import java.util.concurrent.ThreadLocalRandom;

public class BlockTorchflowerCrop extends BlockCrops {
    public BlockTorchflowerCrop() {
        this(0);
    }

    public BlockTorchflowerCrop(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TORCHFLOWER_CROP;
    }

    @Override
    public String getName() {
        return "Torchflower Crop";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.TORCHFLOWER_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(getDamage() == 7 ? getItemId(TORCHFLOWER) : Item.TORCHFLOWER_SEEDS),
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(3) == 0) {
                return 0;
            }
        }
        return super.onUpdate(type);
    }

    @Override
    protected Block getGrowthBlock(int growth) {
        if (getDamage() == 0) {
            return super.getGrowthBlock(1);
        }
        return get(TORCHFLOWER);
    }
}
