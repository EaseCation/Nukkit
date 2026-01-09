package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.Level;

import java.util.concurrent.ThreadLocalRandom;

public class BlockTorchflowerCrop extends BlockCrops {
    BlockTorchflowerCrop() {

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
                Item.get(getDamage() == 7 ? ItemBlockID.TORCHFLOWER : Item.TORCHFLOWER_SEEDS),
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
