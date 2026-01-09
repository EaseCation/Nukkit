package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Leonidius20 on 22.03.17.
 */
public class BlockNetherWart extends BlockFlowable {

    BlockNetherWart() {

    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        Block down = this.down();
        if (down.getId() == SOUL_SAND) {
            return level.setBlock(block, this, true);
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != SOUL_SAND) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(10) == 1) {
                if (this.getDamage() < 0x03) {
                    BlockNetherWart block = (BlockNetherWart) this.clone();
                    block.setDamage(block.getDamage() + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

    @Override
    public int getId() {
        return NETHER_WART;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (this.getDamage() == 0x03) {
            return new Item[]{
                    Item.get(Item.NETHER_WART, 0, ThreadLocalRandom.current().nextInt(2, 5)),
            };
        } else {
            return new Item[]{
                    Item.get(Item.NETHER_WART),
            };
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.NETHER_WART);
    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
